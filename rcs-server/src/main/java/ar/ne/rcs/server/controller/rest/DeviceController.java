package ar.ne.rcs.server.controller.rest;

import ar.ne.rcs.server.serivce.DeviceRegistrationService;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("devices")
public class DeviceController {

    final DeviceRegistrationService service;

    public DeviceController(DeviceRegistrationService service) {
        this.service = service;
    }

    @GetMapping("list")
    public List<DeviceRegistration> listDevices() {
        return service.findAll();
    }
}
