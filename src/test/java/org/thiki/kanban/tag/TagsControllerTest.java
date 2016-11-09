package org.thiki.kanban.tag;

import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


/**
 * Created by xubt on 11/7/16.
 */
@Domain(order = DomainOrder.TAG, name = "标签")
@RunWith(SpringJUnit4ClassRunner.class)
public class TagsControllerTest extends TestBase {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Scenario("创建个人标签>用户可以创建个人标签,以便可以给自己的卡片归纳属性")
    @Test
    public void createPersonalTag() throws Exception {
        given().body("{\"name\":\"tag-name\",\"color\":\"tag-color\"}")
                .header("userName", userName)
                .contentType(ContentType.JSON)
                .when()
                .post("/boards/boardId-foo/tags")
                .then()
                .statusCode(201)
                .body("name", equalTo("tag-name"))
                .body("color", equalTo("tag-color"))
                .body("_links.self.href", equalTo("http://localhost:8007/boards/boardId-foo/tags/fooId"))
                .body("_links.tags.href", equalTo("http://localhost:8007/boards/boardId-foo/tags"));
    }

    @Scenario("获取个人标签>用户为卡片创建个人标签后,可以查看")
    @Test
    public void loadPersonalTags() throws Exception {
        dbPreparation.table("kb_tag")
                .names("id,name,color,author,board_id")
                .values("fooId", "tag-name", "tag-color", "someone", "boardId-foo").exec();

        given().header("userName", userName)
                .when()
                .get("/boards/boardId-foo/tags")
                .then()
                .statusCode(200)
                .body("tags[0].name", equalTo("tag-name"))
                .body("tags[0].color", equalTo("tag-color"))
                .body("tags[0]._links.self.href", equalTo("http://localhost:8007/boards/boardId-foo/tags/fooId"))
                .body("tags[0]._links.tags.href", equalTo("http://localhost:8007/boards/boardId-foo/tags"))
                .body("_links.self.href", equalTo("http://localhost:8007/boards/boardId-foo/tags"));
    }

    @Scenario("更新个人标签>用户创建标签后,可以更新该标签的相关属性")
    @Test
    public void updatePersonalTag() throws Exception {
        dbPreparation.table("kb_tag")
                .names("id,name,color,author,board_id")
                .values("fooId", "tag-name", "tag-color", "someone", "boardId-foo").exec();

        given().body("{\"name\":\"tag-name-new\",\"color\":\"tag-color-new\"}")
                .header("userName", userName)
                .contentType(ContentType.JSON)
                .when()
                .put("/boards/boardId-foo/tags/fooId")
                .then()
                .statusCode(200)
                .body("name", equalTo("tag-name-new"))
                .body("color", equalTo("tag-color-new"))
                .body("_links.self.href", equalTo("http://localhost:8007/boards/boardId-foo/tags/fooId"))
                .body("_links.tags.href", equalTo("http://localhost:8007/boards/boardId-foo/tags"));
    }

    @Scenario("删除个人标签>针对不再使用的标签,用户可以删除")
    @Test
    public void deletePersonalTag() throws Exception {
        dbPreparation.table("kb_tag")
                .names("id,name,color,author,board_id")
                .values("fooId", "tag-name", "tag-color", "someone", "boardId-foo").exec();

        given().header("userName", userName)
                .when()
                .delete("/boards/boardId-foo/tags/fooId")
                .then()
                .statusCode(200)
                .body("_links.tags.href", equalTo("http://localhost:8007/boards/boardId-foo/tags"));
    }
}
