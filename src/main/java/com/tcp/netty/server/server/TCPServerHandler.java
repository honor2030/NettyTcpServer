package com.tcp.netty.server.server;



import com.tcp.netty.server.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
//import org.hibernate.service.spi.ServiceException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2019-04-28.
 */
public class TCPServerHandler extends ChannelDuplexHandler {

    //  클라이언트로부터 오는 데이터 읽기 위함
    private StringBuilder stringBuilder;
    private boolean isChannelReadCompleted=false;

    //  클라이언트로부터 받는 데이터
    private List<Object> byteBufList;

    //  클라이언트 접속 IP, PORT
    private String sourceIP;
    private int port;

    //  생성자 초기화
    public TCPServerHandler(String sourceIP, int port) {

        this.stringBuilder = new StringBuilder();
        this.isChannelReadCompleted = false;

        this.sourceIP = sourceIP;
        this.port = port;
        this.byteBufList = new ArrayList<>();
   }

    //   OS버퍼만큼 지속적으로 읽기
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        byteBufList.add(object);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {

        /*
          전체 데이터를 읽었는지 여부를 체크할 수 있는 로직이 들어가야함
          가령 데이터 앞에 전체 데이터 길이를 넣고 그 데이터 길이 만큼 읽을때까지 리턴
          이유는 클라이언트 접속시 사용 가능한 OS버퍼만큼 데이터를 읽어오므로 버퍼상황에 따라
          전체데이터가 다 읽히지 않을 수 있음
        */

        int size = 0;
        int inSize = 0;
        int i = 0;
        for(Object object : byteBufList) {
            ByteBuf byteBuf = (ByteBuf)object;

            size +=byteBuf.readableBytes();
        }

        byte[] byteArray = new byte[size];

        for(Object object : byteBufList) {
            ByteBuf byteBuf = (ByteBuf) object;

            inSize = byteBuf.readableBytes();

            for(int j = 0; j < inSize; j++) {
                byteArray[i++] = byteBuf.getByte(j);
            }
        }

        //  최종 데이터 합산
        stringBuilder.append(new String(byteArray));


        try {

            String result = processData(stringBuilder.toString());

            writeData(channelHandlerContext, result);

            LogUtil.debug("channelReadComplete : " + result);


        } catch (Exception e) {

            //  오류시 클라이언트로 오류 전송. 보통은 오류코드를 정의해서 사용
            writeData(channelHandlerContext, e.getMessage());

            e.printStackTrace();
        }
    }

    /**
     *
     * @param channelHandlerContext
     * @param throwable
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {

        throwable.printStackTrace();
        channelHandlerContext.close();
    }

    /**
     *
     * @param channelHandlerContext
     * @param data
     */
    private void writeData(
            ChannelHandlerContext channelHandlerContext,
            String data) {

        //  데이터를 쓸때 사용
        ByteBuf dataBuffer = Unpooled.buffer(1024);

        try {

            dataBuffer.writeBytes(data.getBytes());

        } catch (Exception e) {

            e.printStackTrace();

            dataBuffer.writeBytes("error".getBytes());
        }

        channelHandlerContext.writeAndFlush(dataBuffer);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        }
    }

    /**
     * 데이터 처리
     * @param data
     * @return
     * @throws Exception
     */
    private String processData(String data) throws Exception {

        String ret = "error";
        try {

            JSONParser jsonParser = new JSONParser();
            JSONObject resultJSONObject = new JSONObject();


            resultJSONObject = (JSONObject) jsonParser.parse(data);

            //  데이터를 파싱하여 비즈니스 로직 생성. 리턴값으로 클라이언트에 응답을 보냄
            ret = "success";


        } catch (Exception e) {

            throw new ServerException("Error");

        }
        finally {
            return ret;
        }
    }
}
