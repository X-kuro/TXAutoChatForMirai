package com.qin.plugin

import kotlinx.coroutines.launch
import net.kuroi.httpok.repack.TXApiCombine
import net.mamoe.mirai.console.command.ContactCommandSender
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefaultWriteSave
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MemberMuteEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.sendImage
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.sendImage

object AutoRepeatPlugin : PluginBase() {
    private val config = loadConfig("setting.yml")

    val appid by config.withDefaultWriteSave { "2131806391" }
    val appkey by config.withDefaultWriteSave { "gKOHAmYStMsYQ85E" }
    var autoRepeat by config.withDefaultWriteSave { 15 }
    var repeat by config.withDefaultWriteSave { 15 }
    val TxApi = TXApiCombine(appid, appkey)

    override fun onLoad() {
        super.onLoad()
    }

    override fun onEnable() {
        super.onEnable()
        logger.info("Plugin loaded!")

        registerCommand {
            name = "chat"
            alias = listOf("autochat")
            description = "自动说话插件总指令"
            usage = "[/chat autochat x]     随机回复的概率\n" +
                    "[/chat autorepeat x]     随机复读的概率\n" +
                    "更多设置请在插件配置文件中更改"
            onCommand {
                val operatingGroup = if (this is ContactCommandSender && this.contact is Group) {
                    this.contact.id
                } else {
                    0
                }
                if (it.isEmpty()) {
                    return@onCommand false
                }
                if(it.size < 2){
                    return@onCommand false
                }
                when (it[0].toLowerCase()) {
                    "autochat" -> {
                        if (operatingGroup == 0L) {
                            return@onCommand false
                        }
                        autoRepeat = it[1].toInt()
                        this.sendMessage("以将回复频率设置为"+it[1])
                    }
                    "autorepeat"    -> {
                        if (operatingGroup == 0L) {
                            return@onCommand false
                        }
                        repeat = it[1].toInt()
                        this.sendMessage("以将复读频率设置为"+it[1])
                    }
                    else -> {
                        return@onCommand false
                    }
                }
                return@onCommand true
            }
        }

        subscribeGroupMessages {
            this.always {
                 if (message[1].toString() == "[mirai:at:"+bot.id+"]" && !message.content.contains("图片")) {
                     val msg = TxApi.AutoChat(message[2].toString())
                     subject.sendMessage(msg)
                 }
                else if((0..100).random()<=autoRepeat && !message.content.contains("图片")) {
                     val msg = TxApi.AutoChat(message.content)
                     subject.sendMessage(msg)
                 }

                else if((0..100).random()<=repeat && !message.content.contains("图片")) {
                     subject.sendMessage(message.content)
                 }
            }
        }
        subscribeAlways<MemberMuteEvent> {
            it.group.sendMessage(PlainText("恭喜 ${it.member.nameCardOrNick} 喜提禁言套餐一份"))
        }
    }
}