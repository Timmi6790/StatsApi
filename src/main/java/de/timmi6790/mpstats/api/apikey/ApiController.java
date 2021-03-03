package de.timmi6790.mpstats.api.apikey;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiKey/")
@Tag(name = "Internal")
public class ApiController {
    @GetMapping(value = "create")
    public void creatNewApiKey(
    ) {

    }
}
