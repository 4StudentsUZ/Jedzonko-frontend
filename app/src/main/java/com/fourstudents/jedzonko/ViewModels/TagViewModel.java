package com.fourstudents.jedzonko.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fourstudents.jedzonko.Database.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagViewModel extends ViewModel {
    private final MutableLiveData<List<Tag>> tagList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Tag>> getTagList(){return tagList;}

    public List<Tag> getTagsList(){return tagList.getValue();}

    public void setTagList(List<Tag> tags){
        tagList.setValue(tags);
    }


    public int getTagsListSize(){return tagList.getValue().size();}

    public void removeTag(Tag tag){
        List<Tag> tags = tagList.getValue();
        if(tags==null) return;
       tags.remove(tag);
        tagList.setValue(tags);
    }


    public void addTag(Tag tag){
        List<Tag> tags = tagList.getValue();
        if(tags==null) return;
        tags.add(tag);
        tagList.setValue(tags);
    }

    public Tag getTag(int position){
        List<Tag> tags = tagList.getValue();
        Tag tag = tags.get(position);
        return tag;
    }


    public boolean hasTag(Tag tag) {

        List<Tag> tags = tagList.getValue();
        if (tags == null) return false;
        for (Tag tagItem:tags) {
            if(tagItem.getName().equals(tag.getName())) return true;
        }
        return false;
    }

    public void clearTagList() {
        tagList.setValue(new ArrayList<>());
    }

}

