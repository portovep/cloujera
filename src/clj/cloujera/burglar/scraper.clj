(ns cloujera.burglar.scraper
  (:require [clj-http.client :as http]
            [clj-http.cookies :as cookies]))

;; inspiration from: https://github.com/coursera-dl/coursera/blob/master/coursera%2Fcookies.py
(defn- authenticated-cookie-store
  "returns a cookie store that lets you get protected pages"
  [username password]

  (let [cs (cookies/cookie-store)
        a-mooc-url "https://class.coursera.org/modernpoetry-003/lecture"]

    ;; hit a MOOC to get the CSRF token
    (http/get a-mooc-url {:cookie-store cs})

    ;; login
    (let [login-url "https://accounts.coursera.org/api/v1/login"
          csrf-token (:crf_token (cookies/get-cookies cs))
          headers {"Cookie" (str "csrftoken=" csrf-token)
                   "Referer" "https://accounts.coursera.org/signin"
                   "X-CSRFToken" csrf-token}
          data {"email" username
                "password" password
                "webrequest" true}]

      (http/post login-url {:headers headers
                            :form-params  data
                            :cookie-store cs})
      cs)))

;; Username -> Password -> (ProtectedUrl -> Page)
(defn get-protected-page [username password]
  ;; FIXME: authenticated-cookie-store is not quite a referentially transparent
  ;; function........
  ;; serializing the cookie store + caching it with an expiration date would be
  ;; a possible solution
  (let [authenticated-cs ((memoize authenticated-cookie-store) username password)]
    (fn [protected-url]
      (:body (http/get protected-url {:cookie-store authenticated-cs})))))
