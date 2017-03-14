# i5channel
这是对于蹦极本身`Forward`类型消息的一个增强替代。

## 特性
- 可配置是否等待目标服务器可用\*
- 使用缓存的正则匹配投递目标
- 接收方可知投递方信息
- 接收方可知消息投递时间

## 监听
```
I5Channel.listen("any_channel", message -> {
    val sender = message.getSender();
    int sent = message.getSent();
    val input = ByteStreams.newDataInput(message.getBuf());
    ...
});
```

## 发送
```
val message = new ChannelMessage();
message.setLabel("any_channel");
message.setReceiver("^bed-[0-9]+$");// default by all
message.setQueued(true);// default by false
val buf = ByteStreams.newDataOutput();
...
message.setBuf(buf.toByteArray());
I5Channel.send(message);
```
