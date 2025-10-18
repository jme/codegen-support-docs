(defproject simple-webserver "1.0.0"
  :description "Project simple-webserver: A slightly secure, simple web application."
  :url "http://example.com/project-simple-server"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [;; Core Clojure language library. The foundation of the project.
                 [org.clojure/clojure "1.12.0"]

                 ;; The essential HTTP server abstraction for Clojure.
                 [ring/ring-core "1.14.1"]

                 ;; The web server that runs the Ring application.
                 [ring/ring-jetty-adapter "1.14.1"]

                 ;; A concise routing library for Ring, used for clear route definitions.
                 [compojure "1.7.1"]

                 ;; A library for representing HTML in Clojure.
                 [hiccup "2.0.0"]]

    :main ^:skip-aot simple-webserver.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}

  )

