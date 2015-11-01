(ns clj-ansprog.rotations
  )

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
  [[xr yr zr] [pn [cn px py pz]]]
  [pn (into [cn] (->
                   [px py pz]
                   (rx (* (/ Math/PI 2) xr))
                   (ry (* (/ Math/PI 2) yr))
                   (rz (* (/ Math/PI 2) zr))
                   cleanup
                   ))])

(defn- setify-terms
  [t]
  (into #{} t))

(defn unique-pieces
  [pieces]
  (into #{} (map (partial map setify-terms)) pieces))

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
  [r piece]
  (map (partial rotate-part r) piece))


(defn gen-rotations
  "Generates a set of all  rotations for a piece, with each piece normalised to the xyz planes "
  [piece]

  (into #{}
        (map (comp set normalize-piece )
             (for [xr (range 0 4)
                   yr (range 0 4)
                   zr (range 0 4)]
               (set (normalize-piece ( rotate-piece [xr yr zr] piece)))))))