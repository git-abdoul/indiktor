truncate table IKR_VALUE ;
delete from IKR_DEFINITION where IKR_CATEGORY_ID in (select IKR_STATIC_DOMAIN_ID from ikr_category_resource where METRIC_DOMAIN_RESOURCE_ID in (select ID from metric_domain_resource where RESOURCE_NAME = 'THREAD_INFO'));
delete from MONITOR_ACTIVITY where IKR_CATEGORY_RESOURCE_ID in (select ID from IKR_CATEGORY_RESOURCE where METRIC_DOMAIN_RESOURCE_ID in (select ID from metric_domain_resource where RESOURCE_NAME = 'THREAD_INFO'));
delete from IKR_CATEGORY where IKR_STATIC_DOMAIN_ID in (select IKR_STATIC_DOMAIN_ID from IKR_CATEGORY_RESOURCE where METRIC_DOMAIN_RESOURCE_ID in (select ID from metric_domain_resource where RESOURCE_NAME = 'THREAD_INFO'));
delete from IKR_STATIC_DOMAIN where ID in (select IKR_STATIC_DOMAIN_ID from IKR_CATEGORY_RESOURCE where METRIC_DOMAIN_RESOURCE_ID in (select ID from metric_domain_resource where RESOURCE_NAME = 'THREAD_INFO'));
delete from IKR_CATEGORY_RESOURCE where METRIC_DOMAIN_RESOURCE_ID in (select ID from metric_domain_resource where RESOURCE_NAME = 'THREAD_INFO');
delete from METRIC_DOMAIN_RESOURCE where RESOURCE_NAME = 'THREAD_INFO';