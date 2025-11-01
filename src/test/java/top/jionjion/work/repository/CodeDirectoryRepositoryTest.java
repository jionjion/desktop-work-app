package top.jionjion.work.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import top.jionjion.work.BaseTest;
import top.jionjion.work.entity.CodeDirectory;

/**
 * 测试类
 *
 * @author Jion
 */
class CodeDirectoryRepositoryTest extends BaseTest {

    @Autowired
    private CodeDirectoryRepository codeDirectoryRepository;

    @Test
    @Rollback(value = false)
    void save() {
        CodeDirectory codeDirectory = new CodeDirectory();
        codeDirectory.setName("Jion");
        codeDirectory.setPath("D:/Jion");
        codeDirectory.setType("dir");
        codeDirectoryRepository.save(codeDirectory);
    }

    @Test
    @Rollback(value = false)
    void findAll() {
        codeDirectoryRepository.findAll().forEach(System.out::println);
    }

}
