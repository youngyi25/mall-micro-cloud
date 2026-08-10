@echo off
cd /d D:\java\mall-micro-cloud\mall-micro-web
start python serve.py
timeout /t 2
start http://localhost:8080/seckill.html
