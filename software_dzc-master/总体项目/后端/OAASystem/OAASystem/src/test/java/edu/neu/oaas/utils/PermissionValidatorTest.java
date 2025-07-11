package edu.neu.oaas.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Permission Validator Tests")
class PermissionValidatorTest {

    private PermissionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PermissionValidator();
    }

    @Nested
    @DisplayName("Role Validation Tests")
    class RoleValidationTests {
        
        @Test
        @DisplayName("Should correctly identify system admin")
        void testIsSystemAdmin() {
            assertTrue(validator.isSystemAdmin("Admin"));
            assertFalse(validator.isSystemAdmin("TAdmin"));
            assertFalse(validator.isSystemAdmin("User"));
            assertFalse(validator.isSystemAdmin(null));
            assertFalse(validator.isSystemAdmin(""));
            assertFalse(validator.isSystemAdmin("admin")); // case sensitive test
        }

        @Test
        @DisplayName("Should correctly identify tenant admin")
        void testIsTenantAdmin() {
            assertTrue(validator.isTenantAdmin("TAdmin"));
            assertFalse(validator.isTenantAdmin("Admin"));
            assertFalse(validator.isTenantAdmin("User"));
            assertFalse(validator.isTenantAdmin(null));
            assertFalse(validator.isTenantAdmin(""));
            assertFalse(validator.isTenantAdmin("tadmin")); // case sensitive test
        }

        @Test
        @DisplayName("Should correctly identify regular user")
        void testIsRegularUser() {
            assertTrue(validator.isRegularUser("User"));
            assertFalse(validator.isRegularUser("Admin"));
            assertFalse(validator.isRegularUser("TAdmin"));
            assertFalse(validator.isRegularUser(null));
            assertFalse(validator.isRegularUser(""));
            assertFalse(validator.isRegularUser("user")); // case sensitive test
        }

        @Test
        @DisplayName("Should correctly identify admin users")
        void testIsAdminUser() {
            assertTrue(validator.isAdminUser("Admin"));
            assertTrue(validator.isAdminUser("TAdmin"));
            assertFalse(validator.isAdminUser("User"));
            assertFalse(validator.isAdminUser(null));
            assertFalse(validator.isAdminUser(""));
            assertFalse(validator.isAdminUser("admin")); // case sensitive test
        }
    }

    @Nested
    @DisplayName("Information Access Tests")
    class InformationAccessTests {

        @Test
        @DisplayName("Should validate information access permissions")
        void testCanAccessInformation() {
            assertTrue(validator.canAccessInformation("Admin"));
            assertTrue(validator.canAccessInformation("TAdmin"));
            assertFalse(validator.canAccessInformation("User"));
            assertFalse(validator.canAccessInformation(null));
            assertFalse(validator.canAccessInformation(""));
            assertFalse(validator.canAccessInformation("InvalidRole"));
        }

        @Test
        @DisplayName("Should validate information modification permissions")
        void testCanModifyInformation() {
            // System admin can modify any information
            assertTrue(validator.canModifyInformation("Admin", 1, 2));
            assertTrue(validator.canModifyInformation("Admin", null, 1));
            assertTrue(validator.canModifyInformation("Admin", 1, null));

            // Tenant admin can only modify own tenant's information
            assertTrue(validator.canModifyInformation("TAdmin", 1, 1));
            assertFalse(validator.canModifyInformation("TAdmin", 1, 2));
            assertFalse(validator.canModifyInformation("TAdmin", null, 1));
            assertFalse(validator.canModifyInformation("TAdmin", 1, null));

            // Regular user cannot modify information
            assertFalse(validator.canModifyInformation("User", 1, 1));
            assertFalse(validator.canModifyInformation("User", null, 1));
            assertFalse(validator.canModifyInformation("User", 1, null));

            // Invalid role
            assertFalse(validator.canModifyInformation("InvalidRole", 1, 1));
            assertFalse(validator.canModifyInformation(null, 1, 1));
        }

        @Test
        @DisplayName("Should validate information deletion permissions")
        void testCanDeleteInformation() {
            // System admin can delete any information
            assertTrue(validator.canDeleteInformation("Admin", 1, 2));
            assertTrue(validator.canDeleteInformation("Admin", null, 1));
            assertTrue(validator.canDeleteInformation("Admin", 1, null));

            // Tenant admin can only delete own tenant's information
            assertTrue(validator.canDeleteInformation("TAdmin", 1, 1));
            assertFalse(validator.canDeleteInformation("TAdmin", 1, 2));
            assertFalse(validator.canDeleteInformation("TAdmin", null, 1));
            assertFalse(validator.canDeleteInformation("TAdmin", 1, null));

            // Regular user cannot delete information
            assertFalse(validator.canDeleteInformation("User", 1, 1));
            assertFalse(validator.canDeleteInformation("User", null, 1));
            assertFalse(validator.canDeleteInformation("User", 1, null));

            // Invalid role
            assertFalse(validator.canDeleteInformation("InvalidRole", 1, 1));
            assertFalse(validator.canDeleteInformation(null, 1, 1));
        }
    }

    @Nested
    @DisplayName("Conference Management Tests")
    class ConferenceManagementTests {

        @Test
        @DisplayName("Should validate conference access permissions")
        void testCanAccessConference() {
            assertTrue(validator.canAccessConference("Admin"));
            assertTrue(validator.canAccessConference("TAdmin"));
            assertFalse(validator.canAccessConference("User"));
            assertFalse(validator.canAccessConference(null));
            assertFalse(validator.canAccessConference(""));
            assertFalse(validator.canAccessConference("InvalidRole"));
        }

        @Test
        @DisplayName("Should validate conference modification permissions")
        void testCanModifyConference() {
            // System admin can modify any conference
            assertTrue(validator.canModifyConference("Admin", 1, 2));
            assertTrue(validator.canModifyConference("Admin", null, 1));
            assertTrue(validator.canModifyConference("Admin", 1, null));

            // Tenant admin can only modify own tenant's conferences
            assertTrue(validator.canModifyConference("TAdmin", 1, 1));
            assertFalse(validator.canModifyConference("TAdmin", 1, 2));
            assertFalse(validator.canModifyConference("TAdmin", null, 1));
            assertFalse(validator.canModifyConference("TAdmin", 1, null));

            // Regular user cannot modify conferences
            assertFalse(validator.canModifyConference("User", 1, 1));
            assertFalse(validator.canModifyConference("User", null, 1));
            assertFalse(validator.canModifyConference("User", 1, null));

            // Invalid role
            assertFalse(validator.canModifyConference("InvalidRole", 1, 1));
            assertFalse(validator.canModifyConference(null, 1, 1));
        }

        @Test
        @DisplayName("Should validate conference deletion permissions")
        void testCanDeleteConference() {
            // System admin can delete any conference
            assertTrue(validator.canDeleteConference("Admin", 1, 2));
            assertTrue(validator.canDeleteConference("Admin", null, 1));
            assertTrue(validator.canDeleteConference("Admin", 1, null));

            // Tenant admin can only delete own tenant's conferences
            assertTrue(validator.canDeleteConference("TAdmin", 1, 1));
            assertFalse(validator.canDeleteConference("TAdmin", 1, 2));
            assertFalse(validator.canDeleteConference("TAdmin", null, 1));
            assertFalse(validator.canDeleteConference("TAdmin", 1, null));

            // Regular user cannot delete conferences
            assertFalse(validator.canDeleteConference("User", 1, 1));
            assertFalse(validator.canDeleteConference("User", null, 1));
            assertFalse(validator.canDeleteConference("User", 1, null));

            // Invalid role
            assertFalse(validator.canDeleteConference("InvalidRole", 1, 1));
            assertFalse(validator.canDeleteConference(null, 1, 1));
        }
    }

    @Nested
    @DisplayName("Meeting Application Tests")
    class MeetingApplicationTests {

        @Test
        @DisplayName("Should validate meeting application permissions")
        void testCanApplyForMeeting() {
            assertTrue(validator.canApplyForMeeting("Admin"));
            assertTrue(validator.canApplyForMeeting("TAdmin"));
            assertTrue(validator.canApplyForMeeting("User"));
            assertTrue(validator.canApplyForMeeting(null));
            assertTrue(validator.canApplyForMeeting(""));
            assertTrue(validator.canApplyForMeeting("InvalidRole"));
        }

        @Test
        @DisplayName("Should validate meeting application approval permissions")
        void testCanApproveMeetingApplication() {
            // System admin can approve any application
            assertTrue(validator.canApproveMeetingApplication("Admin", 1, 2));
            assertTrue(validator.canApproveMeetingApplication("Admin", null, 1));
            assertTrue(validator.canApproveMeetingApplication("Admin", 1, null));

            // Tenant admin can only approve own tenant's applications
            assertTrue(validator.canApproveMeetingApplication("TAdmin", 1, 1));
            assertFalse(validator.canApproveMeetingApplication("TAdmin", 1, 2));
            assertFalse(validator.canApproveMeetingApplication("TAdmin", null, 1));
            assertFalse(validator.canApproveMeetingApplication("TAdmin", 1, null));

            // Regular user cannot approve applications
            assertFalse(validator.canApproveMeetingApplication("User", 1, 1));
            assertFalse(validator.canApproveMeetingApplication("User", null, 1));
            assertFalse(validator.canApproveMeetingApplication("User", 1, null));

            // Invalid role
            assertFalse(validator.canApproveMeetingApplication("InvalidRole", 1, 1));
            assertFalse(validator.canApproveMeetingApplication(null, 1, 1));
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @DisplayName("Should validate user management access permissions")
        void testCanAccessUserManagement() {
            assertTrue(validator.canAccessUserManagement("Admin"));
            assertTrue(validator.canAccessUserManagement("TAdmin"));
            assertFalse(validator.canAccessUserManagement("User"));
            assertFalse(validator.canAccessUserManagement(null));
            assertFalse(validator.canAccessUserManagement(""));
            assertFalse(validator.canAccessUserManagement("InvalidRole"));
        }

        @Test
        @DisplayName("Should validate user modification permissions")
        void testCanModifyUser() {
            // System admin can modify any user
            assertTrue(validator.canModifyUser("Admin", 1, 2, 1, 2));
            assertTrue(validator.canModifyUser("Admin", null, 1, null, 1));
            assertTrue(validator.canModifyUser("Admin", 1, null, 1, null));

            // Tenant admin can only modify users in their tenant
            assertTrue(validator.canModifyUser("TAdmin", 1, 2, 1, 1));
            assertFalse(validator.canModifyUser("TAdmin", 1, 2, 1, 2));
            assertFalse(validator.canModifyUser("TAdmin", 1, 2, null, 1));
            assertFalse(validator.canModifyUser("TAdmin", 1, 2, 1, null));

            // Regular user can only modify their own profile
            assertTrue(validator.canModifyUser("User", 1, 1, 1, 1));
            assertFalse(validator.canModifyUser("User", 1, 2, 1, 1));
            assertFalse(validator.canModifyUser("User", null, 1, 1, 1));
            assertFalse(validator.canModifyUser("User", 1, null, 1, 1));

            // Invalid role
            assertFalse(validator.canModifyUser("InvalidRole", 1, 1, 1, 1));
            assertFalse(validator.canModifyUser(null, 1, 1, 1, 1));
        }

        @Test
        @DisplayName("Should validate user deletion permissions")
        void testCanDeleteUser() {
            // System admin can delete any user
            assertTrue(validator.canDeleteUser("Admin", 1, 2));
            assertTrue(validator.canDeleteUser("Admin", null, 1));
            assertTrue(validator.canDeleteUser("Admin", 1, null));

            // Tenant admin can only delete users in their tenant
            assertTrue(validator.canDeleteUser("TAdmin", 1, 1));
            assertFalse(validator.canDeleteUser("TAdmin", 1, 2));
            assertFalse(validator.canDeleteUser("TAdmin", null, 1));
            assertFalse(validator.canDeleteUser("TAdmin", 1, null));

            // Regular user cannot delete users
            assertFalse(validator.canDeleteUser("User", 1, 1));
            assertFalse(validator.canDeleteUser("User", null, 1));
            assertFalse(validator.canDeleteUser("User", 1, null));

            // Invalid role
            assertFalse(validator.canDeleteUser("InvalidRole", 1, 1));
            assertFalse(validator.canDeleteUser(null, 1, 1));
        }
    }

    @Nested
    @DisplayName("Department Management Tests")
    class DepartmentManagementTests {

        @Test
        @DisplayName("Should validate department management access permissions")
        void testCanAccessDepartmentManagement() {
            assertTrue(validator.canAccessDepartmentManagement("Admin"));
            assertTrue(validator.canAccessDepartmentManagement("TAdmin"));
            assertFalse(validator.canAccessDepartmentManagement("User"));
            assertFalse(validator.canAccessDepartmentManagement(null));
            assertFalse(validator.canAccessDepartmentManagement(""));
            assertFalse(validator.canAccessDepartmentManagement("InvalidRole"));
        }

        @Test
        @DisplayName("Should validate department modification permissions")
        void testCanModifyDepartment() {
            // System admin can modify any department
            assertTrue(validator.canModifyDepartment("Admin", 1, 2));
            assertTrue(validator.canModifyDepartment("Admin", null, 1));
            assertTrue(validator.canModifyDepartment("Admin", 1, null));

            // Tenant admin can only modify departments in their tenant
            assertTrue(validator.canModifyDepartment("TAdmin", 1, 1));
            assertFalse(validator.canModifyDepartment("TAdmin", 1, 2));
            assertFalse(validator.canModifyDepartment("TAdmin", null, 1));
            assertFalse(validator.canModifyDepartment("TAdmin", 1, null));

            // Regular user cannot modify departments
            assertFalse(validator.canModifyDepartment("User", 1, 1));
            assertFalse(validator.canModifyDepartment("User", null, 1));
            assertFalse(validator.canModifyDepartment("User", 1, null));

            // Invalid role
            assertFalse(validator.canModifyDepartment("InvalidRole", 1, 1));
            assertFalse(validator.canModifyDepartment(null, 1, 1));
        }
    }

    @Nested
    @DisplayName("Tenant Management Tests")
    class TenantManagementTests {

        @Test
        @DisplayName("Should validate tenant management access permissions")
        void testCanAccessTenantManagement() {
            assertTrue(validator.canAccessTenantManagement("Admin"));
            assertFalse(validator.canAccessTenantManagement("TAdmin"));
            assertFalse(validator.canAccessTenantManagement("User"));
            assertFalse(validator.canAccessTenantManagement(null));
            assertFalse(validator.canAccessTenantManagement(""));
            assertFalse(validator.canAccessTenantManagement("InvalidRole"));
        }
    }

    @Nested
    @DisplayName("Approval Management Tests")
    class ApprovalManagementTests {

        @Test
        @DisplayName("Should validate approval management access permissions")
        void testCanAccessApprovalManagement() {
            assertTrue(validator.canAccessApprovalManagement("Admin"));
            assertTrue(validator.canAccessApprovalManagement("TAdmin"));
            assertFalse(validator.canAccessApprovalManagement("User"));
            assertFalse(validator.canAccessApprovalManagement(null));
            assertFalse(validator.canAccessApprovalManagement(""));
            assertFalse(validator.canAccessApprovalManagement("InvalidRole"));
        }
    }

    @Nested
    @DisplayName("Access Validation Tests")
    class AccessValidationTests {

        @Test
        @DisplayName("Should validate tenant access")
        void testValidateTenantAccess() {
            assertTrue(validator.validateTenantAccess(1, 1));
            assertFalse(validator.validateTenantAccess(1, 2));
            assertFalse(validator.validateTenantAccess(null, 1));
            assertFalse(validator.validateTenantAccess(1, null));
            assertFalse(validator.validateTenantAccess(null, null));
            assertFalse(validator.validateTenantAccess(0, 0));
            assertFalse(validator.validateTenantAccess(-1, -1));
        }

        @Test
        @DisplayName("Should validate user access")
        void testValidateUserAccess() {
            assertTrue(validator.validateUserAccess(1, 1));
            assertFalse(validator.validateUserAccess(1, 2));
            assertFalse(validator.validateUserAccess(null, 1));
            assertFalse(validator.validateUserAccess(1, null));
            assertFalse(validator.validateUserAccess(null, null));
            assertFalse(validator.validateUserAccess(0, 0));
            assertFalse(validator.validateUserAccess(-1, -1));
        }
    }
} 