package org.thiki.kanban.projects;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thiki.kanban.TestBase;
import org.thiki.kanban.foundation.annotations.Domain;
import org.thiki.kanban.foundation.annotations.Scenario;
import org.thiki.kanban.foundation.application.DomainOrder;
import org.thiki.kanban.projects.invitation.InvitationCodes;
import org.thiki.kanban.projects.project.ProjectCodes;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertEquals;

/**
 * Created by xutao on 9/12/16.
 */
@Domain(order = DomainOrder.INVITATION, name = "团队邀请")
@RunWith(SpringJUnit4ClassRunner.class)
public class InvitationControllerTest extends TestBase {

    @Scenario("用户可以通过用户名邀请其他成员加入到团队中")
    @Test
    public void inviteOthersWithUserNameToJoinTeam() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(201)
                .body("invitee", equalTo("invitee-user"))
                .body("_links.self.href", endsWith("/projects/foo-project-Id/members/invitation/fooId"))
                .body("_links.project.href", endsWith("/projects/foo-project-Id"));
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_project_member_invitation where project_id='fooId' AND invitee='invitee-user'").size());
    }

    @Scenario("用户可以通过用户名邀请其他成员加入到团队中")
    @Test
    public void inviteOthersWithEmailToJoinTeam() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','thiki2016@163.com','invitee-user','password')");

        given().header("userName", userName)
                .body("{\"invitee\":\"thiki2016@163.com\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(201)
                .body("invitee", equalTo("invitee-user"))
                .body("_links.self.href", endsWith("/projects/foo-project-Id/members/invitation/fooId"))
                .body("_links.project.href", endsWith("/projects/foo-project-Id"));
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_project_member_invitation where project_id='fooId' AND invitee='invitee-user'").size());
    }

    @Scenario("如果邀请人为空,怎不允许发送邀请")
    @Test
    public void NotAllowedIfInviteeIsEmpty() throws Exception {
        given().header("userName", userName)
                .body("{\"invitee\":\"\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(400)
                .body("message", equalTo(InvitationCodes.InviteeIsRequired));
    }

    @Scenario("如果邀请加入的团队并不存在,则不允许发送邀请")
    @Test
    public void NotAllowedIfTeamIsNotExist() throws Exception {
        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(400)
                .body("code", equalTo(ProjectCodes.PROJECT_IS_NOT_EXISTS.code()))
                .body("message", equalTo(ProjectCodes.PROJECT_IS_NOT_EXISTS.message()));
    }

    @Scenario("如果被邀请人不存在,则不允许发送邀请")
    @Test
    public void NotAllowedIfInviteeIsNotExist() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITEE_IS_NOT_EXISTS.code()))
                .body("message", equalTo(InvitationCodes.INVITEE_IS_NOT_EXISTS.message()));
    }

    @Scenario("如果邀请人并非团队的成员则不允许发送邀请")
    @Test
    public void NotAllowedIfInviterIsNotAMemberOfTheTeam() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITER_IS_NOT_A_MEMBER_OF_THE_PROJECT.code()))
                .body("message", equalTo(InvitationCodes.INVITER_IS_NOT_A_MEMBER_OF_THE_PROJECT.message()));
    }

    @Scenario("如果被邀请人已经是团队的成员,则不允许发送邀请")
    @Test
    public void NotAllowedIfInviteeIsAlreadyAMemberOfTheTeam() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-invitee-member-id','foo-project-Id','invitee-user','someone')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITEE_IS_ALREADY_A_MEMBER_OF_THE_PROJECT.code()))
                .body("message", equalTo(InvitationCodes.INVITEE_IS_ALREADY_A_MEMBER_OF_THE_PROJECT.message()));
    }

    @Scenario("如果此前已经存在相同的邀请,则取消之前的邀请")
    @Test
    public void cancelPreviousInvitationBeforeSendingNewInvitation() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_project_member_invitation (id,project_id,inviter,invitee) VALUES ('foo-invitation-Id','foo-project-Id','someone','invitee-user')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(201)
                .body("invitee", equalTo("invitee-user"))
                .body("_links.self.href", endsWith("/projects/foo-project-Id/members/invitation/fooId"))
                .body("_links.project.href", endsWith("/projects/foo-project-Id"));
        assertEquals(1, jdbcTemplate.queryForList("select * from kb_project_member_invitation where id='foo-invitation-Id' AND project_id='foo-project-Id' AND delete_status=1").size());
        assertEquals(1, jdbcTemplate.queryForList("select * from kb_project_member_invitation where project_id='foo-project-Id' AND invitee='invitee-user' AND delete_status=0").size());
    }

    @Scenario("邀请发出后,在消息中心通知用户")
    @Test
    public void addNotificationAfterSendingInvitation() throws Exception {
        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/projects/foo-project-Id/members/invitation")
                .then()
                .statusCode(201)
                .body("invitee", equalTo("invitee-user"))
                .body("_links.self.href", endsWith("/projects/foo-project-Id/members/invitation/fooId"))
                .body("_links.project.href", endsWith("/projects/foo-project-Id"));
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_notification where type='project_member_invitation' AND receiver='invitee-user'").size());
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_project_member_invitation where project_id='fooId' AND invitee='invitee-user'").size());
    }

    @Scenario("查看邀请>当用户接收到邀请后,可以查看邀请的具体内容")
    @Test
    public void loadingInvitationDetailAfterAcceptingInvitation() throws Exception {
        dbPreparation.table("kb_project_member_invitation")
                .names("id,inviter,invitee,project_id")
                .values("invitation-id", "someone", "invitee-user", "foo-project-id")
                .exec();

        given().header("userName", userName)
                .body("{\"invitee\":\"invitee-user\"}")
                .contentType(ContentType.JSON)
                .when()
                .get("/projects/foo-project-Id/members/invitation/invitation-id")
                .then()
                .statusCode(200)
                .body("invitee", equalTo("invitee-user"))
                .body("creationTime", notNullValue())
                .body("isAccepted", equalTo(false))
                .body("_links.self.href", endsWith("/projects/foo-project-Id/members/invitation/invitation-id"))
                .body("_links.project.href", endsWith("/projects/foo-project-Id"));
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_notification where type='project_member_invitation' AND receiver='invitee-user'").size());
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_project_member_invitation where project_id='fooId' AND invitee='invitee-user'").size());
    }

    @Scenario("接受邀请>用户接受邀请,并成功成为团队的一员")
    @Test
    public void AcceptInvitation() throws Exception {
        dbPreparation.table("kb_project_member_invitation")
                .names("id,inviter,invitee,project_id")
                .values("invitation-id", "inviter", "someone", "foo-project-id")
                .exec();

        given().header("userName", userName)
                .when()
                .put("/projects/foo-project-id/members/invitation/invitation-id")
                .then()
                .statusCode(200)
                .body("invitee", equalTo("someone"))
                .body("creationTime", notNullValue())
                .body("isAccepted", equalTo(true))
                .body("_links.self.href", endsWith("/projects/foo-project-id/members/invitation/invitation-id"))
                .body("_links.project.href", endsWith("/projects/foo-project-id"));

        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_members where project_id='foo-project-id' AND user_name='someone'").size());
        assertEquals(1, jdbcTemplate.queryForList("select count(*) from kb_project_member_invitation where project_id='fooId' AND invitee='someone' AND is_accepted=1").size());
    }

    @Scenario("查看邀请>用户查看邀请时,若邀请不存在,则告知用户相关错误")
    @Test
    public void throwExceptionIfInvitationIsNotExistWhenLoadingInvitation() throws Exception {
        given().header("userName", userName)
                .when()
                .get("/projects/foo-project-Id/members/invitation/invitation-id")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITATION_IS_NOT_EXIST.code()))
                .body("message", equalTo(InvitationCodes.INVITATION_IS_NOT_EXIST.message()));
    }

    @Scenario("接受邀请>用户接受邀请时,若邀请不存在,则告知用户相关错误")
    @Test
    public void throwExceptionIfInvitationIsNotExistWhenAcceptingInvitation() throws Exception {
        given().header("userName", userName)
                .when()
                .put("/projects/foo-project-Id/members/invitation/invitation-id")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITATION_IS_NOT_EXIST.code()))
                .body("message", equalTo(InvitationCodes.INVITATION_IS_NOT_EXIST.message()));
    }

    @Scenario("接受邀请>用户接受邀请时,若之前已经接受,则告知用户相关错误")
    @Test
    public void throwExceptionIfInvitationIsAlreadyAccepted() throws Exception {
        dbPreparation.table("kb_project_member_invitation")
                .names("id,inviter,invitee,project_id,is_accepted")
                .values("invitation-id", "someone", "invitee-user", "foo-project-id", 1)
                .exec();

        given().header("userName", userName)
                .when()
                .put("/projects/foo-project-Id/members/invitation/invitation-id")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITATION_IS_ALREADY_ACCEPTED.code()))
                .body("message", equalTo(InvitationCodes.INVITATION_IS_ALREADY_ACCEPTED.message()));
    }

    @Scenario("接受邀请>用户接受邀请时,若已经是团队一员,则告知用户相关错误")
    @Test
    public void throwExceptionIfInviteeIsAlreadyAMember() throws Exception {

        jdbcTemplate.execute("INSERT INTO  kb_project (id,name,author) VALUES ('foo-project-Id','project-name','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_project_member_invitation (id,project_id,inviter,invitee) VALUES ('foo-invitation-Id','foo-project-Id','someone','invitee-user')");
        jdbcTemplate.execute("INSERT INTO  kb_members (id,project_id,user_name,author) VALUES ('foo-project-member-id','foo-project-Id','someone','someone')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('fooUserId','766191920@qq.com','someone','password')");
        jdbcTemplate.execute("INSERT INTO  kb_user_registration (id,email,user_name,password) " +
                "VALUES ('invitee-user-id','766191920@qq.com','invitee-user','password')");

        dbPreparation.table("kb_project_member_invitation")
                .names("id,inviter,invitee,project_id,is_accepted")
                .values("invitation-id", "someone", "invitee-user", "foo-project-id", 1)
                .exec();

        given().header("userName", userName)
                .when()
                .put("/projects/foo-project-Id/members/invitation/invitation-id")
                .then()
                .statusCode(400)
                .body("code", equalTo(InvitationCodes.INVITATION_IS_ALREADY_ACCEPTED.code()))
                .body("message", equalTo(InvitationCodes.INVITATION_IS_ALREADY_ACCEPTED.message()));
    }
}
