package cn.ucai.live.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.data.TestDataRepository;
import cn.ucai.live.data.model.LiveRoom;
import cn.ucai.live.ui.GridMarginDecoration;

import com.bumptech.glide.Glide;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.exceptions.HyphenateException;

import cn.ucai.live.R;
import cn.ucai.live.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;

import static android.media.CamcorderProfile.get;
import static java.security.AccessController.getContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveListFragment extends Fragment {
    private static final String TAG = LiveListFragment.class.getSimpleName();
    private LiveAdapter mAdapter;
    RecyclerView recyclerView;
    GridLayoutManager gm;

    private List<EMChatRoom> chatRoomList;
    private boolean isLoading;
    private boolean isFirstLoading = true;
    private boolean hasMoreData = true;
    private String cursor;
    private int pagenum = 0;
    private final int pagesize = 20;
    private int pageCount = -1;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;
    private EditText etSearch;
    private ImageButton ibClean;
    private List<EMChatRoom> rooms;

    SwipeRefreshLayout mSrl;
    TextView tvRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatRoomList = new ArrayList<EMChatRoom>();
        rooms = new ArrayList<EMChatRoom>();
        mAdapter = new LiveAdapter(getContext(),getLiveRoomList(chatRoomList));
        mSrl = (SwipeRefreshLayout) getView().findViewById(R.id.srl);
        tvRefresh = (TextView) getView().findViewById(R.id.tv_refresh);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycleview);
//        GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
        gm = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridMarginDecoration(6));
        recyclerView.setAdapter(mAdapter);

        footLoadingLayout = (LinearLayout) getView().findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)getView().findViewById(R.id.loading_bar);
        footLoadingText = (TextView) getView().findViewById(R.id.loading_text);
        footLoadingLayout.setVisibility(View.GONE);
        loadAndShowData();
        setListener();
    }

    private void setListener() {
        setChatRoomChangeListener();
        setPullDownListener();
        setPullUpListener();
    }

    private void setPullDownListener() {
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSrl.setRefreshing(true);
                tvRefresh.setVisibility(View.VISIBLE);
                loadAndShowData();
            }
        });
    }

    private void setPullUpListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    int lasPos = gm.findLastVisibleItemPosition();
                    if(hasMoreData && !isLoading && lasPos == mAdapter.getItemCount()-1){
                        loadAndShowData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    private void setChatRoomChangeListener() {
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(new EMChatRoomChangeListener(){

            @Override
            public void onChatRoomDestroyed(String s, String s1) {
                chatRoomList.clear();
                if(mAdapter != null){
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(mAdapter != null){
                                mAdapter.notifyDataSetChanged();
                                loadAndShowData();
                            }
                        }

                    });
                }
            }

            @Override
            public void onMemberJoined(String s, String s1) {

            }

            @Override
            public void onMemberExited(String s, String s1, String s2) {

            }

            @Override
            public void onMemberKicked(String s, String s1, String s2) {

            }
        });
    }


    private void loadAndShowData() {
        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    final EMCursorResult<EMChatRoom> result = EMClient.getInstance().chatroomManager().fetchPublicChatRoomsFromServer(pagesize, cursor);
                    //get chat room list
                    final List<EMChatRoom> chatRooms = result.getData();
                    L.e(TAG,"chatRooms="+chatRooms.size());
                    getActivity().runOnUiThread(new Runnable() {

                        public void run() {
                            mSrl.setRefreshing(false);
                            tvRefresh.setVisibility(View.GONE);
                            chatRoomList.addAll(chatRooms);
//                groupsList.addAll(returnGroups);
                            if (chatRooms.size() != 0) {
                                cursor = result.getCursor();
                                if (chatRooms.size() == pagesize) {
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            if (isFirstLoading) {
                                isFirstLoading = false;
                                mAdapter.initData(getLiveRoomList(chatRoomList));
//                    listView.setAdapter(adapter);

                            } else {
                                if (chatRooms.size() < pagesize) {
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("没有更多数据");
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mSrl.setRefreshing(false);
                            tvRefresh.setVisibility(View.GONE);
                            isLoading = false;
                            footLoadingLayout.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "load failed, please check your network or try it later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * 生成测试数据
     */
    public static List<LiveRoom> getLiveRoomList(List<EMChatRoom> chatRoom) {
        List<LiveRoom> roomList = new ArrayList<>();
        for (EMChatRoom room:chatRoom) {
            LiveRoom liveRoom = new LiveRoom();
            liveRoom.setName(room.getName());
            liveRoom.setAudienceNum(room.getAffiliationsCount());
            liveRoom.setId(room.getOwner());
            liveRoom.setChatroomId(room.getId());
            liveRoom.setCover(EaseUserUtils.getAPPUserInfo(room.getOwner()).getAvatar());
            liveRoom.setAnchorId(room.getOwner());
            roomList.add(liveRoom);
        }

        return roomList;
    }
    static class LiveAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

        private final List<LiveRoom> liveRoomList;
        private final Context context;

        public LiveAdapter(Context context, List<LiveRoom> liveRoomList){
            this.liveRoomList = liveRoomList;
            this.context = context;
        }
        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final PhotoViewHolder holder = new PhotoViewHolder(LayoutInflater.from(context).
                    inflate(R.layout.layout_livelist_item, parent, false));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                  LiveRoom room = liveRoomList.get(position);
                    if (room.getAnchorId().equals(EMClient.getInstance().getCurrentUser())) {
                        context.startActivity(new Intent(context,StartLiveActivity.class)
                        .putExtra("liveroom",room.getId()));
                    }else {
                        context.startActivity(new Intent(context, LiveDetailsActivity.class)
                                .putExtra("liveroom", liveRoomList.get(position)));
                    }

                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            LiveRoom liveRoom = liveRoomList.get(position);
            holder.anchor.setText(liveRoom.getName());
            holder.audienceNum.setText(liveRoom.getAudienceNum() + "人");
            Glide.with(context)
                    .load(liveRoomList.get(position).getCover())
                    .placeholder(R.color.placeholder)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return liveRoomList.size();
        }

        public void initData(List<LiveRoom> liveRoomList) {
            if (liveRoomList!=null)
                liveRoomList.clear();
            liveRoomList.addAll(liveRoomList);
            notifyDataSetChanged();
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo)
        ImageView imageView;
        @BindView(R.id.author)
        TextView anchor;
        @BindView(R.id.audience_num) TextView audienceNum;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
