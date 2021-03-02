package ar.ne.rcs.server.controller.rest;

import ar.ne.rcs.server.serivce.VersionService;
import ar.ne.rcs.shared.models.stores.VersionStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("versionInfo")
public class VersionInfoController {
    private final VersionService versionService;

    public VersionInfoController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("{versionGroup}")
    public VersionStore getVersionForGroup(@PathVariable String versionGroup) {
        return versionService.findByVersionGroup(versionGroup);
    }

    @GetMapping("{identifier}")
    public VersionStore getVersionForDevice(@PathVariable String identifier) {
        return versionService.findByDevice(identifier);
    }
}
