package com.jpsite.shortLink;

import com.jpsite.shortLink.ShortLink;
import org.springframework.data.repository.CrudRepository;

/**
 * @author jiangpeng
 * @date 2019/11/2715:29
 */
public interface ShortLinkRepository extends CrudRepository<ShortLink, Long> {

}
