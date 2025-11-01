package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import top.jionjion.work.entity.CodeDirectory;

/**
 * 码目录仓库
 *
 * @author Jion
 */
@Repository
public interface CodeDirectoryRepository extends JpaRepository<CodeDirectory, Long> {

}
