package egovframework.com.config;

import java.util.HashMap;
import java.util.Map;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.DefaultPaginationManager;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import egovframework.com.cmm.EgovComTraceHandler;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.ImagePaginationRenderer;
import egovframework.com.cmm.web.EgovMultipartResolver;

@Configuration
@ComponentScan(basePackages = "egovframework", includeFilters = {
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Service.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class)
}, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
})
@Import({
	EgovConfigAppAspect.class,
	EgovConfigAppDatasource.class,
	EgovConfigAppIdGen.class,
	EgovConfigAppProperties.class,
	EgovConfigAppMapper.class,
	EgovConfigAppTransaction.class,
	EgovConfigAppValidator.class,
	EgovConfigAppWhitelist.class
})
@PropertySources({
	@PropertySource("classpath:/egovframework/egovProps/globals.properties")
}) //CAUTION: min JDK 8
public class EgovConfigApp {

	/**
	 * @return AntPathMatcher 등록.  Ant 경로 패턴 경로와 일치하는지 여부를 확인
	 */
	@Bean
	public AntPathMatcher antPathMatcher() {
		return new AntPathMatcher();
	}

	/**
	 * @return [Resource 설정] 메세지 Properties 경로 설정
	 */
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		reloadableResourceBundleMessageSource.setBasenames(
			"classpath:/egovframework/message/com/message-common",
			"classpath:/org/egovframe/rte/fdl/idgnr/messages/idgnr",
			"classpath:/org/egovframe/rte/fdl/property/messages/properties");
		reloadableResourceBundleMessageSource.setCacheSeconds(60);
		return reloadableResourceBundleMessageSource;
	}

	/**
	 * @return [Resource 설정] 메세지 소스 등록
	 */
	@Bean
	public EgovMessageSource egovMessageSource() {
		EgovMessageSource egovMessageSource = new EgovMessageSource();
		egovMessageSource.setReloadableResourceBundleMessageSource(messageSource());
		return egovMessageSource;
	}

	/**
	 * @return [LeaveaTrace 설정] defaultTraceHandler 등록
	 */
	@Bean
	public EgovComTraceHandler defaultTraceHandler() {
		return new EgovComTraceHandler();
	}

	/**
	 * @return [LeaveaTrace 설정] traceHandlerService 등록. TraceHandler 설정
	 */
	@Bean
	public DefaultTraceHandleManager traceHandlerService() {
		DefaultTraceHandleManager defaultTraceHandleManager = new DefaultTraceHandleManager();
		defaultTraceHandleManager.setReqExpMatcher(antPathMatcher());
		defaultTraceHandleManager.setPatterns(new String[] {"*"});
		defaultTraceHandleManager.setHandlers(new TraceHandler[] {defaultTraceHandler()});
		return defaultTraceHandleManager;
	}

	/**
	 * @return [LeaveaTrace 설정] LeaveaTrace 등록
	 */
	@Bean
	public LeaveaTrace leaveaTrace() {
		LeaveaTrace leaveaTrace = new LeaveaTrace();
		leaveaTrace.setTraceHandlerServices(new TraceHandlerService[] {traceHandlerService()});
		return leaveaTrace;
	}

	/**
	 * @return [ImagePaginationRenderer 설정] ImagePaginationRenderer 등록
	 */
	@Bean
	public ImagePaginationRenderer imageRenderer() {
		return new ImagePaginationRenderer();
	}

	/**
	 * @return [ImagePaginationRenderer 설정] defaultPaginationManager 설정.
	 */
	@Bean
	public DefaultPaginationManager paginationManager() {
		DefaultPaginationManager defaultPaginationManager = new DefaultPaginationManager();

		Map<String, PaginationRenderer> rendererType = new HashMap<>();
		rendererType.put("image", imageRenderer());
		defaultPaginationManager.setRendererType(rendererType);

		return defaultPaginationManager;
	}

	/**
	 * @return [MultipartResolver 설정] CommonsMultipartResolver 등록
	 */
	@Bean
	public CommonsMultipartResolver springRegularCommonsMultipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(100000000);
		commonsMultipartResolver.setMaxInMemorySize(100000000);
		return commonsMultipartResolver;
	}

	/**
	 * @return [MultipartResolver 설정] EgovMultipartResolver 등록
	 */
	@Bean
	public EgovMultipartResolver localMultiCommonsMultipartResolver() {
		EgovMultipartResolver egovMultipartResolver = new EgovMultipartResolver();
		egovMultipartResolver.setMaxUploadSize(100000000);
		egovMultipartResolver.setMaxInMemorySize(100000000);
		return egovMultipartResolver;
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return localMultiCommonsMultipartResolver();
	}
}
