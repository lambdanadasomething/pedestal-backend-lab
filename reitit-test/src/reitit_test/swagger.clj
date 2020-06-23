(ns reitit-test.swagger
  (:require [io.pedestal.http :as server]
            [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.pedestal :as pedestal]
            
            [reitit.dev.pretty :as pretty]
            [reitit.spec :as rs]
            [reitit.coercion.malli]
            [reitit.http.interceptors.dev :as dev]
            [muuntaja.core :as m]
            [reitit.http.coercion :as coercion]
            [reitit.http.interceptors.parameters :as parameters]
            [reitit.http.interceptors.muuntaja :as muuntaja]
            [reitit.http.interceptors.exception :as exception]
            [reitit.http.interceptors.multipart :as multipart]))


(def main-router
  (http/router
   ["/demo"
    ["/posts/:post-name/comments/:comment-id" 
     {:get {:parameters {:path [:map 
                                [:post-name :string] 
                                [:comment-id [int?]]]}
            :responses {200 {:body [any?]}}
            :handler (fn [{{{:keys [post-name comment-id]} :path} :parameters}]
                       {:status 200
                        :body {:name post-name
                               :cmt comment-id
                               :rnd (rand-int 30) }})}}]]
 ;option map for http/router
   {:reitit.interceptor/transform dev/print-context-diffs ;; pretty context diffs
    :validate rs/validate ;; enable spec validation for the routes itself
    :exception pretty/exception ;; debug incase the route format is wrong
    :data {:coercion reitit.coercion.malli/coercion
           :muuntaja m/instance
           :interceptors [
                             ;; query-params & form-params
                          (parameters/parameters-interceptor)
                             ;; content-negotiation
                          (muuntaja/format-negotiate-interceptor)
                             ;; encoding response body
                          (muuntaja/format-response-interceptor)
                             ;; exception handling
                          (exception/exception-interceptor)
                             ;; decoding request body
                          (muuntaja/format-request-interceptor)
                             ;; coercing response bodys
                          (coercion/coerce-response-interceptor)
                             ;; coercing request parameters
                          (coercion/coerce-request-interceptor)
                             ;; multipart
                          (multipart/multipart-interceptor)]}}))

(defn make-server-config
  [reitit-routes]
  (-> {::server/type :jetty
       ::server/port 3000
       ::server/join? false
       ;; no pedestal routes
       ::server/routes []}
      (server/default-interceptors)
      ;; swap the reitit router
      (pedestal/replace-last-interceptor
       (pedestal/routing-interceptor reitit-routes))
      (server/dev-interceptors)))

(def myserver
  (server/create-server (make-server-config main-router)))
