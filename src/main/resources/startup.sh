#!/bin/sh
#
# Phenomena Server startup script
# Created by gugo

java -Dlogback.configurationFile=../config/logger.xml -cp ../lib/*:phenomena-server.jar ltg.ps.PhenomenaServer ../config/server.xml &

