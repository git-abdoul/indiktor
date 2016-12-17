select cache_name, max(capture_millis) from cache_metrics group by cache_name

select cache_metrics.* from cache_metrics inner join (select cache_name, max(capture_millis) as cap_max from cache_metrics group by cache_name) as max_cap_cache 
on cache_metrics.cache_name=max_cap_cache.cache_name and cache_metrics.capture_millis=max_cap_cache.cap_max