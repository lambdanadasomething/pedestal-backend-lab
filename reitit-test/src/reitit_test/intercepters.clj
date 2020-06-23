(ns reitit-test.intercepters
  (:require [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.interceptor.sieppari :as sieppari]))

(defn interceptor [number]
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

(def app
  (http/ring-handler
   (http/router
    ["/api"
     {:interceptors [(interceptor 1)]}

     ["/number"
      {:interceptors [(interceptor 10)]
       :get {:interceptors [(interceptor 100)]
             :handler (fn [req]
                        {:status 200
                         :body (select-keys req [:number])})}}]])
   ;; the default handler
   (ring/create-default-handler)
    ;; executor
   {:executor sieppari/executor}))


(app {:request-method :get, :uri "/"})
; {:status 404, :body "", :headers {}}

(app {:request-method :get, :uri "/api/number"})
; {:status 200, :body {:number 111}}

(+ 1 1)

