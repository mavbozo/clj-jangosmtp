# clj-jangosmtp

http based API for JangoSMTP API.

## Usage

leinigen project.clj

```
[clj-jangosmtp "0.1.2"]
```

Sample Code:

```clojure
(require '[clj-jangosmtp.core :refer :all])

;; create the component first
(def j (jangosmtp-api "Username" "Password"))

(check-bounce j "me@me.com")

```

## License

Copyright © 2014 Avicenna

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
