#!/usr/bin/bash
# copy script

# CHMOD:
chmod -R 775 ./reports/dev
find ./reports/dev -type f -exec chmod 644 {} \;

# RSYNC:
rsync -vv -e ssh --times --recursive reports/dev/ tomcat@qa:~tomcat/tomcat-5.0.27/reports