package ar.ne.rcs.shared.models.frp;

import lombok.Data;

import java.util.List;

@Data
public class TCPInfo {
    public List<Proxy> proxies;

    enum Status {
        online, offline
    }

    public static class Proxy {
        public String name;
        public Config config;
        public int today_traffic_in;
        public int today_traffic_out;
        public int cur_conns;
        public String last_start_time;
        public String last_close_time;
        public Status status;
    }

    @Data
    public static class Config {
        public String proxy_name;
        public String proxy_type;
        public boolean use_encryption;
        public boolean use_compression;
        public String group;
        public String group_key;
        public String proxy_protocol_version;
        public String bandwidth_limit;
        public Object metas;
        public int local_ip;
        public String local_port;
        public String plugin;
        public String plugin_params;
        public String health_check_type;
        public int health_check_timeout_s;
        public int health_check_max_failed;
        public int health_check_interval_s;
        public String health_check_url;
        public int remote_port;

    }
}
