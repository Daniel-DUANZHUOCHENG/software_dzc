package edu.neu.oaas.service;

import edu.neu.oaas.mapper.UserBehaviorMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.pojo.UserBehavior;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

//    public void addUser(User user,int departmentId) {
//        userMapper.insertUser(user);
//        userDepartmentMapper.insertUserDepartment(user.getId(), departmentId);
//    }

    public List<User> getAllUser(){
        return userMapper.getAllUser();
    }

    public List<User> getUsersByTenantId(int tenantId) {
        return userMapper.getUsersByTenantId(tenantId);
    }
    public List<User> getUserByDepartmentId(Integer departmentId){
        return userMapper.getUserByDepartmentId(departmentId);
    }

    public List<User> searchUsers(String username, String phoneNumber, String status, LocalDate startDate, LocalDate endDate) {
        return userMapper.searchUsers(username, phoneNumber, status, startDate, endDate);
    }

    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Integer id){
        return userMapper.getUserById(id);
    }

    public java.util.List<User> findByPath(String pathPreFix){
        return userMapper.findByPathPrefix(pathPreFix);
    }

    @Cacheable(value = "userCache", key = "#name")
    public User getUserByName(String name){
        return userMapper.getUserByName(name);
    }

    @CachePut(value = "userCache", key = "#user.id")
    public boolean insertUser(User user) {
        Integer id = user.getDepartmentId();
        String path = departmentMapper.getDepartmentById(id).getPath()+"_u";
        userMapper.insertUser(user);
        return true;
    }

    @CacheEvict(value = "userCache", key = "#id")
    public boolean deleteById(Integer id){
        userMapper.deleteById(id);
        return true;
    }

    @CachePut(value = "userCache", key = "#user.id")
    public boolean updateUser(User user) {
        userMapper.updateUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getGender(),
                user.getDepartmentId(),
                user.getStatus(),
                user.getRole(),
                user.getCreatedAt(),
                user.getPosition(),
                user.getRemark(),
                user.getAvatar(),
                user.getTenantId(),
                user.getPath()
        );
        return true;
    }
    //添加用户
    @Transactional
    public void addUser(User user, int departmentId) {
        userMapper.insertUser2(user);
        Integer userId = user.getId();
        if (userId != null) {
//            userDepartmentMapper.insertUserDepartment(userId, departmentId);
        } else {
            throw new IllegalStateException("User ID is null after insertion.");
        }
    }

    //注册用户
    public void registerUser(User user) {
        // Hash the password before saving the user
        user.setPassword(user.getPassword());
        userMapper.insertUser2(user);
    }

    @Transactional
    public void deleteUserByTenantId(Integer tenantId) {
        userMapper.deleteByTenantId(tenantId);
    }

    //登录

    @Cacheable(value = "userCache", key = "#username")
    public User login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User loginUser(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user == null ) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    //修改个人信息和密码
    public void updateUserInfo(User user) {
        userMapper.updateUser2(user);
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        User user = userMapper.getUserById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword); // 确保密码加密
            userMapper.updateUser2(user);
            return true;
        }
        return false;
    }

    public User getUserProfile(int userId) {
        User user = userMapper.getUserById(userId);
        if (user != null && (user.getAvatar() == null || user.getAvatar().isEmpty())) {
            user.setAvatar(DEFAULT_AVATAR); // 设置默认头像
        }
        return user;
    }

//    private String hashPassword(String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt());
//    }

    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String UPLOAD_DIR = PROJECT_PATH + "/avatar/";
    private static final String DEFAULT_AVATAR = "/avatar/default.jpg"; // 默认头像路径


    public String saveAvatar(MultipartFile file, int userId) throws IOException {
        System.out.println("🔍 开始保存头像 - 用户ID: " + userId + ", 上传目录: " + UPLOAD_DIR);
        
        User user = userMapper.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // 确保上传目录存在
        Path uploadDirPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDirPath)) {
            System.out.println("📁 创建头像上传目录: " + UPLOAD_DIR);
            Files.createDirectories(uploadDirPath);
        }

        // Delete old avatar if it exists and is not the default avatar
        if (user.getAvatar() != null && !user.getAvatar().equals(DEFAULT_AVATAR)) {
            try {
                String oldFileName = user.getAvatar().substring(8); // Remove leading "/avatar/"
                Path oldAvatarPath = Paths.get(UPLOAD_DIR, oldFileName);
                if (Files.exists(oldAvatarPath)) {
                    Files.delete(oldAvatarPath);
                    System.out.println("🗑️ 删除旧头像: " + oldAvatarPath);
                }
            } catch (Exception e) {
                System.err.println("⚠️ 删除旧头像失败: " + e.getMessage());
                // 继续执行，不影响新头像保存
            }
        }

        // Save new avatar
        String fileName = userId + "_avatar" + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        
        System.out.println("💾 保存新头像到: " + filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update user's avatar in the database
        String avatarUrl = "/avatar/" + fileName;
        user.setAvatar(avatarUrl);
        userMapper.updateUser2(user);
        
        System.out.println("✅ 头像保存完成 - 数据库路径: " + avatarUrl);
        return avatarUrl;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    //分页
    public Map<String, Object> getUsersByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.getUsersByPage(offset, pageSize);
        int total = userMapper.getTotalUserCount();
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("total", total);
        return result;
    }

    public void importUsers(MultipartFile file) throws IOException {
        System.out.println("importUsers");
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过标题行
            try {
                User user = new User();
                user.setId(getCellValueAsInt(row.getCell(0)));
                user.setUsername(getCellValueAsString(row.getCell(1)));
                user.setPassword(getCellValueAsString(row.getCell(2)));
                user.setNickname(getCellValueAsString(row.getCell(3)));
                user.setPhoneNumber(getCellValueAsString(row.getCell(4)));
                user.setEmail(getCellValueAsString(row.getCell(5)));
                user.setGender(getCellValueAsString(row.getCell(6)));
                user.setDepartmentId(getCellValueAsInt(row.getCell(7)));
                user.setStatus(getCellValueAsString(row.getCell(8)));
                user.setRole(getCellValueAsString(row.getCell(9)));
                user.setCreatedAt(convertStringToTimestampOrNow(getCellValueAsString(row.getCell(10))).toLocalDateTime());
                user.setPosition(getCellValueAsString(row.getCell(11)));
                user.setRemark(getCellValueAsString(row.getCell(12)));
                user.setAvatar(getCellValueAsString(row.getCell(13)));
                user.setTenantId(getCellValueAsInt(row.getCell(14)));
                user.setPath(getCellValueAsString(row.getCell(15)));
                System.out.println(user);

                userMapper.insertUser(user);
            } catch (Exception e) {
                System.out.println("Error processing row " + row.getRowNum());
                e.printStackTrace();
            }
        }
        workbook.close();
    }

    private int getCellValueAsInt(Cell cell) {
        if (cell == null) {
            return 0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid integer value in cell: " + cell.getStringCellValue());
                    return 0;
                }
            default:
                System.out.println("Cannot get integer value from cell type: " + cell.getCellType());
                return 0;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    private Timestamp convertStringToTimestampOrNow(String strDate) {
        if (strDate == null || strDate.isEmpty()) {
            return new Timestamp(System.currentTimeMillis());
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(strDate);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }

    public Map<String, Object> generateUserPortrait(Integer userId) {
        User user = userMapper.getUserById(userId);
        Map<String, Object> portrait = new HashMap<>();
        if (user != null) {
            portrait.put("gender", user.getGender());
            portrait.put("behaviorStats", getUserBehaviorStats(userId));
        }
        return portrait;
    }

    private Map<String, Object> getUserBehaviorStats(Integer userId) {
        List<UserBehavior> behaviors = userBehaviorMapper.getUserBehaviorsByUserId(userId);
        Map<String, Object> stats = new HashMap<>();
        long pageViews = behaviors.stream().filter(b -> "page_load".equals(b.getAction())).count();
        long videoPlay = behaviors.stream().filter(b -> "video_play".equals(b.getAction())).count();
        stats.put("pageViews", pageViews);
        stats.put("videoPlay", videoPlay);
        return stats;
    }

    public List<Map<String, Object>> generateAllUserPortraits() {
        List<User> users = userMapper.getAllUser();
        List<Map<String, Object>> portraits = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> portrait = new HashMap<>();
            portrait.put("gender", user.getGender());
            portrait.put("behaviorStats", getUserBehaviorStats(user.getId()));
            portraits.add(portrait);
        }
        return portraits;
    }








}


