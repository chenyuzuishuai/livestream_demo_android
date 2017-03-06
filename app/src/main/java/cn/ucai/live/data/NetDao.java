package cn.ucai.live.data;

import android.content.Context;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.domain.User;

import java.io.File;

import cn.ucai.live.I;
import cn.ucai.live.utils.MD5;
import cn.ucai.live.utils.OkHttpUtils;
import cn.ucai.live.utils.OnCompleteListener;

import static com.baidu.location.b.k.co;


/**
 * Created by yu chen on 2017/2/8.
 */

public class NetDao {
    /**
     * 注册请求.Post请求
     */
    public static void register(Context context, String username, String nick, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.NICK, nick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 取消注册
     */
    public static void unregister(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 登录
     */
    public static void login(Context context, String username, String password,
                             OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfoByUsername(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 更新用户昵称
     */
    public static void updateUsernick(Context context, String username, String usernick, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.NICK, usernick)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 更新用户头像
     */
    public static void uploadAPPUserAvatar(Context context, String username, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, username)
                .addParam(I.AVATAR_TYPE, I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 添加好友
     */
    public static void addContact(Context context, String username, String cname, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, cname)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 从服务器下载所有好友信息
     */
    public static void loadContact(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 删除联系人
     */
    public static void deleteAPPContact(Context context, String username, String cname,
                                        OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, cname)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 创建群组
     */
    public static void createGroup(Context context, EMGroup group, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID, group.getGroupId())
                .addParam(I.Group.NAME, group.getGroupName())
                .addParam(I.Group.DESCRIPTION, group.getDescription())
                .addParam(I.Group.OWNER, group.getOwner())
                .addParam(I.Group.IS_PUBLIC, String.valueOf(group.isPublic()))
                .addParam(I.Group.ALLOW_INVITES, String.valueOf(group.isAllowInvites()))
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 添加群组成员
     */
    public static void addGroupMembers(Context context, String members, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME, members)
                .addParam(I.Member.GROUP_HX_ID, hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 移除群组成员
     */
    public static void removeGroupMember(Context context, String hxid, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER)
                .addParam(I.Member.GROUP_HX_ID, hxid)
                .addParam(I.Member.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 更新群组名称
     */
    public static void updateGroupName(Context context, String hxid, String groupName, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_GROUP_NAME)
                .addParam(I.Group.HX_ID, hxid)
                .addParam(I.Group.NAME, groupName)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 退出群组操作
     */
    public static void deleteGroup(Context context, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP)
                .addParam(I.Group.HX_ID, hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 创建群组
     */
    public static void createLive(Context context, User user, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_CHATROOM)
                .addParam("auth", "1IFgE")
                .addParam("name", user.getMUserNick() + "的直播间")
                .addParam("description", user.getMUserNick() + "的瓜皮直播")
                .addParam("ower", user.getMUserName())
                .addParam("maxusers", "300")
                .addParam("members", user.getMUserName())
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 关闭直播间
     */
    public static void removeLive(Context context, String chatroomId, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CHATROOM)
                .addParam("auth", "1IFgE")
                .addParam("chatRoomId", chatroomId)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 下载礼物数据
     */
    public static void loadAllGift(Context context, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_GIVING_GIFT)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 获取零钱
     */
    public static void loadChange(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_BALANCE)
                .addParam("uname", username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 发送礼物
     */
    public static void givingGifts(Context context,String username,String auchor,int giftId,int count,
                                   OnCompleteListener<String> listener){
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_GIVING_GIFT)
                .addParam("username",username)
                .addParam("auchor",auchor)
                .addParam("giftId",String.valueOf(giftId))
                .addParam("count",String.valueOf(count))
                .targetClass(String.class)
                .execute(listener);
    }
}
