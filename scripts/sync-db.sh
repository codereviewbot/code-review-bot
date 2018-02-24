#!/usr/bin/env bash
set -e

regex='^{?"([^"]+)"[^"]+"([^"]+)"'

echo 'loading env from ".lein-env"...'
cat .lein-env | (while read line
do
    set -e
    [[ "${line}" =~ $regex ]]
    var="${BASH_REMATCH[1]}"
    val="${BASH_REMATCH[2]}"
    eval "export ${var}=${val}"
done

echo 'running mongo script...'
echo "
var remote = connect('${REMOTE_MONGODB_URI}');
var local = connect('${MONGODB_URI}');

['configs', 'users'].forEach(function(coll) {
    local[coll].remove({});
    local[coll].insertMany(remote[coll].find({}).toArray());
});
" | mongo > /dev/null)

echo 'finished'
