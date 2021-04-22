package de.timmi6790.mpstats.api.versions.v1.java.group;

import de.timmi6790.mpstats.api.versions.v1.common.group.GroupController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/group")
@Tag(name = "Java - Group")
public class JavaGroupController extends GroupController {
    @Autowired
    public JavaGroupController(final JavaGroupService javaGroupService) {
        super(javaGroupService);
    }
}
