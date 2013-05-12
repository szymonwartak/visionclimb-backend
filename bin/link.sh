#!/bin/bash

if [ $# -eq 0 ]
  then
    DEV_DIR=/Users/Szymon/mutina/dev
else
    DEV_DIR=$1
fi

echo $DEV_DIR

SOURCE_DIR=$DEV_DIR/backend/public
TARGET_DIR=$DEV_DIR/PG1/assets/www

mkdir -p $SOURCE_DIR/js/jquery.ui.map
mkdir -p $SOURCE_DIR/css
mkdir -p $SOURCE_DIR/css/images
mkdir -p $SOURCE_DIR/images

# need to be hard links so the web server picks them up
ln -sfv $TARGET_DIR/index.html $SOURCE_DIR/asdf.html
ln -sfv $TARGET_DIR/images/asdf.png $SOURCE_DIR/images/asdf.png
ln -sfv $TARGET_DIR/js/camera.js $SOURCE_DIR/js/camera.js
ln -sfv $TARGET_DIR/js/climage.js $SOURCE_DIR/js/climage.js
ln -sfv $TARGET_DIR/js/jquery.js $SOURCE_DIR/js/jquery.js
ln -sfv $TARGET_DIR/js/jquery.mobile.js $SOURCE_DIR/js/jquery.mobile.js
ln -sfv $TARGET_DIR/js/phonegap.js $SOURCE_DIR/js/phonegap.js
ln -sfv $TARGET_DIR/js/rest.js $SOURCE_DIR/js/rest.js
ln -sfv $TARGET_DIR/js/data.js $SOURCE_DIR/js/data.js
ln -sfv $TARGET_DIR/js/geo.js $SOURCE_DIR/js/geo.js
ln -sfv $TARGET_DIR/js/jquery.form.js $SOURCE_DIR/js/jquery.form.js
ln -sfv $TARGET_DIR/js/todataurl.js $SOURCE_DIR/js/todataurl.js
ln -sfv $TARGET_DIR/js/predef.js $SOURCE_DIR/js/predef.js
ln -sfv $TARGET_DIR/js/log4javascript.js $SOURCE_DIR/js/log4javascript.js
ln -sfv $TARGET_DIR/css/jquery.mobile.structure.css $SOURCE_DIR/css/jquery.mobile.structure.css
ln -sfv $TARGET_DIR/css/jquery.mobile.theme.css $SOURCE_DIR/css/jquery.mobile.theme.css
ln -sfv $TARGET_DIR/css/jquery.mobile.css $SOURCE_DIR/css/jquery.mobile.css
ln -sfv $TARGET_DIR/css/images/ajax-loader.gif $SOURCE_DIR/css/images/ajax-loader.gif
ln -sfv $TARGET_DIR/js/jquery.ui.map/jquery.ui.map.js $SOURCE_DIR/js/jquery.ui.map/jquery.ui.map.js
ln -sfv $TARGET_DIR/js/jquery.ui.map/jquery.ui.map.services.js $SOURCE_DIR/js/jquery.ui.map/jquery.ui.map.services.js
