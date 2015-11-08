(ns clj-ansprog.rotations
  (:require [clj-ansprog.tools :as tools]
            [clj-ansprog.core :as asp]))

(defn rz
  [[x y z] q]
  [(- (* x (Math/cos q)) (* y (Math/sin q)))
   (+ (* x (Math/sin q)) (* y (Math/cos q)))
   z])

(defn rx
  [[x y z] q]
  [x
   (- (* y (Math/cos q)) (* z (Math/sin q)))
   (+ (* y (Math/sin q)) (* z (Math/cos q)))])

(defn ry
  [[x y z] q]
  [(+ (* z (Math/sin q)) (* x (Math/cos q)))
   y
   (- (* z (Math/cos q)) (* x (Math/sin q)))])

(defn cleanup
  [coord]
  (map #(Math/round %)   coord))

(defn rotate-part
  "rotates an element of the form [:part [:p x y z]]"
  [[xr yr zr] [pn [cn px py pz]]]
  [pn (into [cn] (->
                   [px py pz]
                   (rx (* (/ Math/PI 2) xr))
                   (ry (* (/ Math/PI 2) yr))
                   (rz (* (/ Math/PI 2) zr))
                   cleanup
                   ))])


(defn v-diff
  [[ax ay az] [bx by bz]]
  [(- ax bx) (- ay by) (- az bz)])


(defn min-corner
  "find the left/back/bottom-most corner of the piece"
  [piece]
  (reduce
    (fn [[mx my mz] [_ [_ x y z]]]
      [(min mx x) (min my y) (min mz z)])
    [4 4 4]
    piece))


(defn translate-part
  "translate a part by a vector"
  [p [x y z]]
  (for [[pt [pv px py pz]] p]
    [pt [pv (+ x px) (+ y py) (+ z pz)]]))

(defn normalize-piece
  "Move a piece so that it's as close to the origin as it will go"
  [piece]
  (translate-part piece (v-diff [0 0 0] (min-corner piece))))

(defn rotate-piece
  "rotates a piece  of the form [[:part [:p x y z]]]"

  [r piece]
  (map (partial rotate-part r) piece))


(defn  gen-rotations
  "Generates a set of all  rotations for a piece, with each piece normalised to the xyz planes "
  [piece]

  (into #{}
        (map (comp set normalize-piece )
             (for [xr (range 0 4)
                   yr (range 0 4)
                   zr (range 0 4)]
               (set (normalize-piece (rotate-piece [xr yr zr] piece)))))))


(defn
  generate-parts-cmd
  [sln]
  (as-> (asp/anssets sln) $
        (map asp/all-terms $)
        (map gen-rotations $)
        (into #{} $)
        (map first $)
        (map asp/->InMemAnswerSet $)
        (tools/nest-solutions $)
        ))

(defn in-range?
  [x min max]
  (and (< x max) (>= x min)))

(defn piece-in-box?
  [parts]
  (every? (fn [[_ [_ & coord]]]
            (every? (fn [v] (and (< v 3)  (>= v 0))) coord)) parts))

(defn gen-piece-locations
  "generate part images  for each possible rotation and translation (excluding duplicates)
   generates terms of the form: placepart(r(RX,RY,RZ),t(TX,TY,TZ),p(1,0,0)) for locations within the 3x3 cube
   Input is in the form of a list of parts   [:part [:p x y z]]"
  [parts]
  (println "rotating " parts )
  (let [kvs
        (for [rx (range 0 4)
              ry (range 0 4)
              rz (range 0 4)
              tx (range 0 3)
              ty (range 0 3)
              tz (range 0 3)]

          (as-> (rotate-piece [rx ry rz] parts) $
                (normalize-piece $)
                (translate-part $ [tx ty tz])
                (set $)
                [$ [[:r rx ry rz] [:t tx ty tz]]]))]
    (as->
      kvs $
      (filter (fn [[part _]] (piece-in-box? part)) $)
      (reduce conj {} $)
      (map (fn [[piece [rot trn]]]
             (-> piece
                 (tools/nest-terms trn)
                 (tools/nest-terms rot)
                 (tools/rename-terms :placepart))) $)
      ;(mapcat identity $)
      )))

(defn generate-spun-parts-cmd
  []
  )

    ;(tools/nest-terms [:r rx ry rz])
    ;   (tools/nest-terms [:t tx ty tz])
    ;    (tools/rename-terms :placepart)
    ;))
