package com.web.moudle.setting.chooser.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.web.common.base.BaseAdapter;
import com.web.common.base.BaseViewHolder;
import com.web.common.base.OnItemClickListener;
import com.web.common.util.ResUtil;
import com.web.moudle.setting.chooser.bean.LocalItem;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;

public class LocalChooserAdapter extends BaseAdapter<LocalItem> {
    private OnItemClickListener<LocalItem> itemClickListener;
    private AdapterView.OnItemSelectedListener itemSelectListener;
    private int select = -1;
    private int selectColor = ResUtil.getColor(R.color.lightBlue);



    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position, @Nullable LocalItem item) {
        if (item == null) return;

        if (item.getType() == LocalItem.TYPE_DIR) {
            holder.bindText(R.id.tv_fileName, item.getName());
            holder.bindText(R.id.tv_tagStart, item.getTagStart());
            holder.bindText(R.id.tv_date, item.getDate());
            holder.bindImage(R.id.iv_fileIcon, R.drawable.folder);
        } else if (item.getType() == LocalItem.TYPE_FILE) {
            holder.bindText(R.id.tv_fileName, item.getName());
            holder.bindText(R.id.tv_tagStart, item.getTagStart());
            holder.bindText(R.id.tv_date, item.getDate());
            holder.bindImage(R.id.iv_fileIcon, R.drawable.defaultfile);
        } else if (item.getType() == LocalItem.TYPE_BACK) {
            holder.bindText(R.id.tv_name,item.getName());
            holder.bindImage(R.id.iv_back,R.drawable.icon_back_black);
        }

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> itemClickListener.itemClick(item, position));
        }

        if (position == select) {
            holder.itemView.setBackgroundColor(selectColor);
        } else {
            //**此处drawable不能共用公共drawable，否则会出现bug
            holder.itemView.setBackground(ResUtil.getDrawable(R.drawable.selector_transparent_gray));
        }

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==LocalItem.TYPE_BACK){
            return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_item_back, parent, false));
        }else{
            return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_choose, parent, false));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(getData()==null)return 0;
        return getData().get(position).getType();
    }

    public int getSelect() {
        return select;
    }

    public void select(int index) {
        if (this.select == index) return;

        if (select > 0 && select < getItemCount()) {
            notifyItemChanged(select);
        }
        this.select = index;
        if (select > 0 && select < getItemCount()) {
            notifyItemChanged(select);
            if(itemSelectListener!=null){
                itemSelectListener.onItemSelected(null,null,select,0);
            }
        }else{
            if(itemSelectListener!=null){
                itemSelectListener.onNothingSelected(null);
            }
        }
    }

    public void setItemClickListener(OnItemClickListener<LocalItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemSelectListener(AdapterView.OnItemSelectedListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

}
