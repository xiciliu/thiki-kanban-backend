package net.thiki.repo.mybatis.mappers;

import java.util.List;
import net.thiki.kanban.domain.entry.Entry;
import net.thiki.kanban.domain.entry.EntryExample;
import org.apache.ibatis.annotations.Param;

public interface EntryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int countByExample(EntryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int deleteByExample(EntryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int insert(Entry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int insertSelective(Entry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    List<Entry> selectByExample(EntryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    Entry selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int updateByExampleSelective(@Param("record") Entry record, @Param("example") EntryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int updateByExample(@Param("record") Entry record, @Param("example") EntryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int updateByPrimaryKeySelective(Entry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table th_entry
     *
     * @mbggenerated Wed Apr 13 18:38:34 CST 2016
     */
    int updateByPrimaryKey(Entry record);
}