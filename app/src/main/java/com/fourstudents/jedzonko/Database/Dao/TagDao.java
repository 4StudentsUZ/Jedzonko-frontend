package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Tag;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TagDao {
    @Insert(onConflict = REPLACE)
    void insert(Tag tag);

    @Delete
    void delete(Tag tag);

    @Update
    void update(Tag tag);

    @Query("SELECT * FROM tag")
    List<Tag> getAllTags();
}
