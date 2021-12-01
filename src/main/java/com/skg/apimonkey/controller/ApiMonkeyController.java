package com.skg.apimonkey.controller;

import com.skg.apimonkey.domain.data.SwaggerData;
import com.skg.apimonkey.domain.data.UserDataCase;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.repository.SwaggerDataRepository;
import com.skg.apimonkey.repository.UserDataCaseRepository;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.util.MappingUtil;
import com.skg.apimonkey.service.util.WebUtil;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ApiMonkeyController {

    @Autowired
    private SwaggerParserService parserService;
    @Autowired
    private DataCreationService dataCreationService;
    @Autowired
    private SwaggerDataRepository swaggerDataRepository;
    @Autowired
    private UserDataCaseRepository userDataCaseRepository;

    @GetMapping("/")
    public String homePage(Model model) {

        model.addAttribute("title", "APIMonkey");
        model.addAttribute("description", "The No-code API testing solution");
        model.addAttribute("robots", "index");

        return "index";
    }

    @GetMapping("/why-api-monkey")
    public String whyApiMonkeyPage(Model model) {

        model.addAttribute("title", "Why API Monkey");
        model.addAttribute("description", "Why API Monkey page");
        model.addAttribute("robots", "index");

        return "why_api_monkey_page";
    }

    @GetMapping("/blog")
    public String blogPage(Model model) {

        model.addAttribute("title", "Blog");
        model.addAttribute("description", "Blog apiMonkey page");
        model.addAttribute("robots", "index");

        return "blog_page";
    }

    @GetMapping("/pricing")
    public String pricingPage(Model model) {

        model.addAttribute("title", "Pricing");
        model.addAttribute("description", "Pricing apiMonkey page");
        model.addAttribute("robots", "index");

        return "pricing_page";
    }

    @GetMapping("/contact-us")
    public String contactUsPage(Model model) {

        model.addAttribute("title", "Contact us");
        model.addAttribute("description", "Contact us page");
        model.addAttribute("robots", "index");

        return "contact_us_page";
    }

    @GetMapping("/get-started")
    public String getStartedPage(Model model,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        model.addAttribute("title", "Get started");
        model.addAttribute("description", "Get started with testing your API's");
        model.addAttribute("robots", "noindex");

        return "get_started_page";
    }

    @GetMapping("/run-tests")
    public String runTestsPage(Model model,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               @RequestParam(value = "api", required = false) String hashId) {

        List<TestDataCase> cases = null;
        Map<String, TestDataCase> casesFromDatabase = new HashMap<>();
        String errorMessage = null;
        int variantNumber = 4;

        SwaggerData swaggerData = swaggerDataRepository.findFirstByHashId(hashId);
        SwaggerParseResult result = parserService.getSwaggerData(swaggerData);

        if (Objects.nonNull(result) && Objects.nonNull(result.getOpenAPI())) {

            List<UserDataCase> dataCases = userDataCaseRepository.findBySwaggerData(swaggerData);
            if (CollectionUtils.isNotEmpty(dataCases)) {
                casesFromDatabase = dataCases.stream()
                        .map(MappingUtil::toTestDataCase)
                        .collect(Collectors.toMap(TestDataCase::getDataId, Function.identity()));
            }

            try {
                cases = dataCreationService.generateTestDataCases(result, variantNumber);
                for (int i = 0; i < cases.size(); i++) {
                    if(casesFromDatabase.containsKey(cases.get(i).getDataId())) {
                        cases.set(i, casesFromDatabase.get(cases.get(i).getDataId()));
                    }
                }
            } catch (Exception e) {
                log.error("generateTestDataCases error: ", e);
                errorMessage = "Error creating test cases from swagger specification. Current URL is not supported.";
            }

        } else {

            return WebUtil.response404(response);
        }

        model.addAttribute("variantNumber", variantNumber);
        model.addAttribute("cases", cases);
        model.addAttribute("passedUrl", swaggerData.getPassedUrl());
        model.addAttribute("swaggerDataHashId", swaggerData.getHashId());

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("title", "Run API's tests");
        model.addAttribute("description", "Run tests for API");
        model.addAttribute("robots", "noindex");

        return "run_tests_page";
    }
}