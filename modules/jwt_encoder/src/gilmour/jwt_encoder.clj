(ns gilmour.jwt-encoder
  (:require
   [buddy.core.keys :as keys]
   [buddy.sign.jwt :as jwt]
   [com.stuartsierra.component :as c]))

(defprotocol JwtEncoder
  (encode [this data])
  (decode [this data]))

(defrecord SHASigner [config]
  JwtEncoder
  (encode [_ data]
    (jwt/sign data (:secret config) {:alg (:algorithm config)}))
  (decode [_ data]
    (jwt/unsign data (:secret config) {:alg (:algorithm config)})))

(defn make-sha-signer
  [config]
  (map->SHASigner {:config config}))

(defrecord AsymetricSigner [config public-key private-key]
  JwtEncoder
  (encode [_ data]
    (jwt/sign data public-key {:alg (:algorithm config)}))
  (decode [_ data]
    (jwt/unsign data private-key {:alg (:algorithm config)}))

  c/Lifecycle
  (start [this]
    (let [public-key  (-> config :keypair :public-key keys/public-key)
          private-key (-> config :keypair :private-key keys/private-key)]
      (assoc this :public-key public-key :private-key private-key)))
  (stop [this]
    (assoc this :public-key nil :private-key nil)))

(defn make-asymetric-signer
  [config]
  (map->AsymetricSigner {:config config}))

(def sha-signer-algs
  #{:hs256 :hs512})

(def asymetric-signer-algs
  #{:es256 :es512 :ps256 :ps512 :rs256 :rs512})

(defn make-jwt-encoder
  [{:keys [algorithm] :as config}]
  (cond
    (sha-signer-algs algorithm)
    (make-sha-signer config)

    (asymetric-signer-algs algorithm)
    (make-asymetric-signer config)))
