package com.donkey.util;

public class AppKeys {
	// database name
	public static String DATABASE_NAME = "DonkeyGo.db";
	// databse table name
	public static final String MEMORY_TABLE_NAME = "memory";
	public static final String LANDMARK_TABLE_NAME = "landmark";

	// landmark pic url
	public static final String PIC_FOLDER_NAME = "/mnt/sdcard/donkeyGo/";

	// Login check url
	public static final String LOGIN_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!login";
	// register
	public static final String REGISTER_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!register";
	// comment url
	public static final String MEMORY_COMMENT_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryCommentAction!save";
	// get comment list url
	public static final String MEMORY_COMMENT_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryCommentAction!getMemoryComments?memoryid=";
	// get memory url
	public static final String GET_MEMORY_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryAction!getMemories?nums=5";
	// add memory url
	public static final String ADD_MEMORY_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryAction!save";
	// get memory detail url
	public static final String MEMORY_DETAIL_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryAction!getMemoryDetails?memoryid=";
	// get travel list url
	public static final String GET_TRAVEL_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!getTravels?nums=$nums&uid=$uid";
	// get travel detail url
	public static final String TRAVEL_DETAIL_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!getTravelDetails?travelId=";
	// add travel plan
	public static final String ADD_TRAVEL_PLAN = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!saveTravel";
	// author detail
	public static final String GET_AUTHOR_DETAIL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!getUserDetail?user_id=";
	// add travel comment
	public static final String ADD_TRAVEL_COMMENT_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelCommentAction!saveTravelComment";
	// add location
	public static final String ADD_LOCATION_URL = "http://219.217.227.33:8080/DonkeyGoServer/landmarkAction!save";
	// my memory list url
	public static final String MY_MEMORY_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryAction!getMyMemories?userid=";
	// trvael comment list url
	public static final String TRAVEL_COMMENT_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelCommentAction!getAllTravelComments?travelid=";
	// get more memory list
	public static final String GET_MORE_MEMORY_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/memoryAction!getMoreMemories?nums=20&since_id=";
	// get more travel list
	public static final String GET_MORE_TRAVEL_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!getMoreTravels?nums=20&since_id=";
	// add travel attention
	public static final String ADD_TRAVEL_ATTENTION_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!addFollowers";
	// add friend
	public static final String ADD_FRIEND_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!setApply";
	// add memory attention
	public static final String ADD_MEMORY_AUTHOR_ATTENTION_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!setApply?userid=$userid&comuserid=";
	// landmark list url
	public static final String LANDMARK_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/landmarkAction!getLandmarks?memoryid=";
	// search travel
	public static final String SEARCH_TRAVEL_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!searchTravels?cond=";
	// send message
	public static final String SEND_MESSAGE_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!sendMessage";
	public static final String SEND_MSG_TO_FRIEND_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!sendMessageToFriend";
	//get group list url
	public static final String GET_GROUP_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!returnGroups?user_id=";
	//get group detail
	public static final String GET_GROUP_DETAIL_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!returnFriendMemories?user_id=$user_id&group_id=";
	// get apply list url
	public static final String GET_APPLY_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!returnApply?userid=$userid&type=1";
	// get message list url
	public static final String GET_MSG_LIST_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!returnApply?userid=$userid&type=0";
	// friend apply 1:accept 0:reject
	public static final String HANDLE_FRIEND_APPLY_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!addFriend?userid=$userid&comuserid=$comuser&type=";
	// read message
	public static final String READ_MESSAGE_URL = "http://219.217.227.33:8080/DonkeyGoServer/userAction!deleteMessage?messageid=";
	//get my attention travel
	public static final String GET_MY_ATTENTION_TRAVEL_URL = "http://219.217.227.33:8080/DonkeyGoServer/travelAction!getMyTravels?user_id=";

	// 头像在SD卡上的缓存路径
	public static final String AVATAR_CACHE_FOLDER = "/mnt/sdcard/donkey/avatar/";
}
