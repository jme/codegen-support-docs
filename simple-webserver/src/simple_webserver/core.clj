(ns simple-webserver.core
  "Core namespace handling HTTP routing, middleware, and server lifecycle.

   Component Relations:
   - Uses Ring middleware for request processing (resource, content-type, security).
   - Defines route handlers for the home page and 404 errors.
   - Manages the Jetty server lifecycle via the -main function.

   State Management:
   - Stateless; uses Ring's immutable request/response model exclusively."
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [hiccup.page :as page])
  (:gen-class))

(def ^:private security-headers
  "A map of security-related HTTP headers. This is a security invariant
  and is applied to every response."
  {"X-Frame-Options" "DENY"
   "X-Content-Type-Options" "nosniff"
   "X-XSS-Protection" "1; mode=block"
   "Strict-Transport-Security" "max-age=31536000; includeSubDomains"
   "Content-Security-Policy" "default-src 'none'; style-src 'self'; img-src 'self'; frame-ancestors 'none'"})

(defn wrap-security-headers
  "Ring middleware to merge a predefined set of security headers into the response."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (update response :headers merge security-headers))))

(defn- layout
  "Defines the primary HTML page structure using Hiccup syntax.
  It includes the main stylesheet and a content placeholder."
  [& content]
  (page/html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "simple-webserver"]
    (page/include-css "/css/style.css")]
   [:body
    [:div.content content]]))

(defn- home-handler
  "Handles requests for the root path ('/'). Returns a 200 OK response
  with the home page HTML, vertically centered."
  []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (layout [:h1 "X Y Z Z Y"])})

(defn- not-found-handler
  "Handles all requests that do not match a defined route. Returns a 404
  Not Found response with a custom HTML body."
  []
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body (layout [:h1 "You have entered the void. There is no one here. There is no place to go. There is no lamp."])})

(defroutes app-routes
  "The core routing definition for the application, created with Compojure."
  (GET "/" [] (home-handler))
  (route/not-found (not-found-handler)))

(def app
  "The final, composed Ring application handler. It is constructed by applying
  a chain of middleware to the base routes."
  (-> app-routes
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-security-headers)))

(defn -main
  "The application entry point. Starts a Jetty web server."
  [& _]
  (let [port (Integer. (or (System/getenv "PORT") "3000"))]
    (println (str "Starting web server on port " port))
    (jetty/run-jetty app {:port port})))

