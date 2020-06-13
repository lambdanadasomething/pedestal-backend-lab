(defproject swagger-data "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [metosin/ring-swagger "0.26.2"]
                 [frankiesardo/route-swagger "0.1.4"]
                 [pedestal-api "0.3.4"]
                 [swagger-spec "0.5.0"]
                 [io.swagger.parser.v3/swagger-parser "2.0.20"]
                 [com.walmartlabs/lacinia "0.37.0-alpha-2"]]
  :main ^:skip-aot swagger-data.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {}})
