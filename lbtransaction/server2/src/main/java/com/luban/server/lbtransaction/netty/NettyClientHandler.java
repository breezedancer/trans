package com.luban.server.lbtransaction.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luban.server.lbtransaction.transactional.LbTransaction;
import com.luban.server.lbtransaction.transactional.LbTransactionManager;
import com.luban.server.lbtransaction.transactional.TransactionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受数据:" + msg.toString());
        JSONObject jsonObject = JSON.parseObject((String) msg);

        String groupId = jsonObject.getString("groupId");
        String command = jsonObject.getString("command");

        System.out.println("接收command:" + command);
        // 对事务进行操作


        LbTransaction lbTransaction = LbTransactionManager.getLbTransaction(groupId);
        if (command.equals("rollback")) {
            lbTransaction.setTransactionType(TransactionType.rollback);
        } else if (command.equals("commit")) {
            lbTransaction.setTransactionType(TransactionType.commit);
        }
        lbTransaction.getTask().signalTask();
    }

    public synchronized Object call(JSONObject data) throws Exception {
        context.writeAndFlush(data.toJSONString());
        return null;
    }
}
