(ns gilmour.jwt-encoder
  (:require
   [buddy.core.keys :as keys]
   [buddy.sign.jwt :as jwt]
   [com.stuartsierra.component :as c]))

(defprotocol JwtEncoder
  (encode [this data])
  (decode [this data]))

(defrecord SHASigner [algorithm secret]
  JwtEncoder
  (encode [_ data]
    (jwt/sign data secret {:alg algorithm}))
  (decode [_ data]
    (jwt/unsign data secret {:alg algorithm})))

(defn make-sha-signer
  [config]
  (map->SHASigner config))

(defrecord AsymetricSigner [algorithm password keypair public-key private-key]
  JwtEncoder
  (encode [_ data]
    (jwt/sign data private-key {:alg algorithm}))
  (decode [_ data]
    (jwt/unsign data public-key {:alg algorithm}))

  c/Lifecycle
  (start [this]
    (let [public-key  (keys/public-key (:public-key keypair))
          private-key (if password
                        (keys/private-key (:private-key keypair) password)
                        (keys/private-key (:private-key keypair)))]
      (assoc this :public-key public-key :private-key private-key)))
  (stop [this]
    (assoc this :public-key nil :private-key nil)))

(defn make-asymetric-signer
  [config]
  (map->AsymetricSigner config))

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
