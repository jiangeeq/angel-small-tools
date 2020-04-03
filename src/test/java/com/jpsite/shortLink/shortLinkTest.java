package com.jpsite.shortLink;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * @author jiangpeng
 * @date 2020/4/316:36
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class shortLinkTest {
    @Autowired
    private ShortLinkRepository shortLinkRepository;
    @Autowired
    private HttpServletRequest request;

    @Test
    public void getLocalAddress() throws UnknownHostException {
        StringBuilder sb = new StringBuilder();
        //获取服务器域名
        String serverName = request.getServerName();
        //获取服务器端口
        int serverPort = request.getServerPort();
        //获取服务器IP地址;
        InetAddress address = InetAddress.getByName(request.getServerName());
        String hostAddress = address.getHostAddress();
        System.out.println(hostAddress);
        String localAddress = sb.append("http://").append(serverName).append(":").append(serverPort).toString();
        System.out.println(localAddress);
    }

    /**
     * 生成短链接并解析短链接
     */
    @Test
    public void testCreateShortLink() {
        Instant instant = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        ShortLink shortLink = shortLinkRepository.save(new ShortLink("https://juejin.im/user/5b6a41ef5188251ac858752a/posts", Date.from(instant)));
        String shortUrl = ConversionUtils.encode(shortLink.getId(), 4);

        log.info("生成的短链接码为: [{}]", shortUrl);

        long id = ConversionUtils.decode(shortUrl);
        Optional<ShortLink> shortLinkOpt = shortLinkRepository.findById(id);
        String url = shortLinkOpt.isPresent() ? shortLinkOpt.get().getUrl() : "";
        log.info("短链接[{}]的目标url是[{}]", shortUrl, url);
    }
}
