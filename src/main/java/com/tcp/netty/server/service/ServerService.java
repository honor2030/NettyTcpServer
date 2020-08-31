package com.tcp.netty.server.service;

import com.tcp.netty.server.server.TCPServer;

public class ServerService {

    /**
     * 서버 서비스 시작
     */
    public static boolean start() {


        boolean bRet = false;

        try {

            // ServerService 시작
            Thread serverThread = new Thread(new TCPServer(5000));
            serverThread.start();

            //  여러 서버 사용시 추가

            //  정상처리시 true 리턴
            bRet = true;

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return bRet;
        }
    }


}
