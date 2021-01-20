package ar.ne.rcs.server.controller;

import ar.ne.rcs.proto.RCS;
import ar.ne.rcs.server.repo.DeviceRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("rc")
@RestController
public class RC {
    final DeviceRepo repo;

    public RC(DeviceRepo repo) {
        this.repo = repo;
    }

    @PostMapping("add/disposable")
    public long addDisposable() {
        return repo.exec("token", "command", RCS.CommandType.DISPOSABLE);
    }
}
