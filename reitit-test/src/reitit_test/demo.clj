(ns reitit-test.demo
  (:require [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.spec :as rs]
            [reitit.dev.pretty :as pretty]
            [reitit.coercion :as coercion]
            [reitit.coercion.malli]
            [malli.core :as m]))

(def router
  (r/router
   [["/api/ping" ::ping]
    ["/api/orders/:id" ::order-by-id]]))

(r/match-by-path router "/api/orders/45")

(r/match-by-name router ::order-by-id {:id 3})

(defn handler [_]
  {:status 200, :body "ok"})

(defn wrap [handler id]
  (fn [request]
    (update (handler request) :wrap (fnil conj '()) id)))

(def app
  (ring/ring-handler
   (ring/router
    ["/api" {:middleware [[wrap :api]]}
     ["/ping" {:get handler
               :name ::ping}]
     ["/admin" {:middleware [[wrap :admin]]}
      ["/users" {:get handler
                 :post handler}]]])))

(app {:request-method :get, :uri "/api/ping"})

(r/router
 ["/api" {:handler "identity"}]
 {:validate rs/validate
  :exception pretty/exception})

; Coercion is more complicated...

(m/validator [:string])

;{:path {:company (m/validator [:string])
;         :user-id (m/validator [int?])}}

(def router2
  (r/router
   ["/:company/users/:user-id" {:name ::user-view
                                :coercion reitit.coercion.malli/coercion
                                :parameters {:path [:map 
                                                    [:company :string]
                                                    [:user-id [int?]]]}}]
   {:compile coercion/compile-request-coercers}))

(coercion/coerce!
 (r/match-by-path router2 "/metosin/users/123"))
