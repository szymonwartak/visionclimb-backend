java -Dorg.mortbay.jetty.Request.maxFormContentSize=500000 \
	-XX:MaxPermSize=128M -Xmx1024M -Xss2M -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launcher.jar "$@"
