package top.jionjion.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.jionjion.work.entity.BaiLianLog;

/**
 * 百炼平台调用日志仓库
 *
 * @author Jion
 */
@Repository
public interface BaiLianLogRepository extends JpaRepository<BaiLianLog, Long> {

}
