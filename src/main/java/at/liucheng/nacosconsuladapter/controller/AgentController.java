package at.liucheng.nacosconsuladapter.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AgentController {

    public static final String DATE_CENTER_CONF = "{\n" +
            "    \"Config\": {\n" +
            "        \"Datacenter\": \"default\"\n" +
            "    }\n" +
            "}";

    @GetMapping(value = "/v1/agent/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getNodes() {
        //todo这里先暂时写死
        return DATE_CENTER_CONF;
    }
}