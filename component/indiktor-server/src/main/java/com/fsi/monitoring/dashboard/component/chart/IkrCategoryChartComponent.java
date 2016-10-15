package com.fsi.monitoring.dashboard.component.chart;



public class IkrCategoryChartComponent {
//extends IkrChartComponent  {	
//	private static final long serialVersionUID = 2364996631664745316L;	
//	
//	private Map<Integer, Collection<MetricGroupBean>> metricBeanMap;
//	String chartKeys;
//	
//	private ConcurrentHashMap<Long, List<IkrValueBean>> ikrCategoryValues;
//	
//	public IkrCategoryChartComponent(String componentId,
//									 String title, 
//									 Collection<MetricGroupBean> metricBeans, 
//									 String style,
//									 int width,
//									 int height,
//									 int maxSlot,
//									 String type,
//									 DataModelPM dataModelPM) {
//		super(componentId,title,style,"categoryChart",width, height, maxSlot, type);
//		
//		ikrCategoryValues = new ConcurrentHashMap<Long, List<IkrValueBean>>();		
//		metricBeanMap = new HashMap<Integer, Collection<MetricGroupBean>>();
////		chartKeys = "";
//		
////		int i = 0;
//		for(MetricGroupBean metricBean : metricBeans) {
//			IkrCategory ikrCategory = metricBean.getIkrCategory();	
//			
//			Collection<MetricGroupBean> beans = metricBeanMap.get(ikrCategory.getId());
//			if (beans == null) {
//				beans = new ArrayList<MetricGroupBean>();
//				metricBeanMap.put(ikrCategory.getId(), beans);
//			}
//			beans.add(metricBean);
//			
////			String key = metricBean.getEnv()+metricBean.getIkrContext()+ikrCategory.getId();
////			if (i==0)
////				chartKeys = key;
////			else
////				chartKeys = chartKeys + ":" + key;
////			i = i + 1;			
//			this.ikrIds.add((long)ikrCategory.getId());
////			this.monitorIds.add(metricBean.getMonitorConfig().getId());
//		}
//	}		
//	
//	public String getIkrCategoryKeys() {
//		return chartKeys;
//	}
//
//	@Override
//	protected boolean isAccepted(IkrValueBean ikrValueBean) {
//		IkrDefinitionBean ikrDefinitionBean = ikrValueBean.getIkrDefinitionBean();
//		long ikrCatId = (long)ikrDefinitionBean.getIkrCategory().getId();
//		if (ikrIds.contains(ikrCatId)) {
//			Collection<MetricGroupBean> beans = metricBeanMap.get(ikrDefinitionBean.getIkrCategory().getId());			
//			for(MetricGroupBean bean : beans) {			
//				String domain = (bean.getDomainView()==null)?"":bean.getDomainView();
//				if (bean.getContext().equals(ikrDefinitionBean.getContext()) && 
//					bean.getLogicalEnv().getId() == ikrDefinitionBean.getLogicalEnv().getId() &&
//					domain.equals(ikrDefinitionBean.getDomainView())) {					
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}	
//	
//	public void push(RealTimeBean valueBean) {		
//		IkrValueBean ikrValueBean = (IkrValueBean)valueBean;			
//		if (isAccepted(ikrValueBean)) {
//			long id = ikrValueBean.getIkrDefinitionBean().getIkrCategory().getId();	
//			FormattedValue format = ikrValueBean.getFormattedValue();
//			FormattedValue oldFormat = formats.get(id);
//			String axisLabel = ikrValueBean.getIkrDefinitionBean().getIkrCategory().getLabel();
//			if (oldFormat == null) {
//				formats.put(id, format);
//				String unit = format.getIkrUnit().getSymbol();
//				axisLabel = axisLabel + ((unit.length()>0)?" ("+unit+")":"");
//				axisLabels.put(id, axisLabel);
//			}
//			else {
//				String oldUnit = oldFormat.getIkrUnit().getSymbol();
//				double oldDivider = oldFormat.getIkrUnit().getDivider().doubleValue();
//				String unit = format.getIkrUnit().getSymbol();				
//				double divider = format.getIkrUnit().getDivider().doubleValue();
//				if (!oldUnit.equals(unit) && divider<oldDivider) {					
//					formats.put(id, format);
//					axisLabel = axisLabel + ((unit.length()>0)?" ("+unit+")":"");
//					axisLabels.put(id, axisLabel);
//				}
//			}			
//			synchronized (ikrCategoryValues) {
//				List<IkrValueBean> values = ikrCategoryValues.get(id);
//				if(values == null) {
//					values = new ArrayList<IkrValueBean>();
//					ikrCategoryValues.put(id, values);
//				}
//				if (values.size() == MAX_CAPACITY)
//					values.remove(0);
//				values.add(ikrValueBean);
//			}
//		}	
//	}
//
//	public void computeComponent() {
//		chartData = "";
//		for (List<IkrValueBean> tmp : ikrCategoryValues.values()) {
//			if(tmp.size()>0)
//				chartData = chartData + "#FINDTS#" + getFlux(tmp);
//		}	
//		
//		if (chartData.length()>0)
//			chartData = chartData.substring(8);	
//	}
//	
//	@Override
//	public String getChartUnits() {
//		List<String> labels = new ArrayList<String>(axisLabels.values());
//		String label = "";
//		if(labels.size()>0)
//			label = labels.get(0);
//		return label;
//	}
//	
//	@Override
//	protected String buildData(IkrValueBean bean) {
//		long ikrDefId = bean.getIkrDefinitionBean().getIkrDefinition().getId();
//		long catId = bean.getIkrDefinitionBean().getIkrCategory().getId();
//		String categoryLabel = bean.getIkrDefinitionBean().getIkrCategory().getLabel();
//		String instance = bean.getIkrDefinitionBean().getIkrDefinition().getFullIkrInstance();
//		String context = bean.getIkrDefinitionBean().getContext();
//		String label = categoryLabel + " : " + instance + "@" + context;
//		long captureTime = bean.getIkrValue().getCaptureTime().getTime();
//		IkrUnit toUnit = formats.get(catId).getIkrUnit();
//		String toUnitSymbol = formats.get(catId).getIkrUnit().getSymbol();
//		FormattedValue formattedValue = bean.getFormattedValue();
//		IkrUnit unit = formattedValue.getIkrUnit();
//		String unitSymbol = formattedValue.getIkrUnit().getSymbol();
//		String value = formattedValue.getValue();
//		if (!toUnitSymbol.equals(unitSymbol)) {
//			value = convertValue(value, unit, toUnit);				
//		}						
//		return ikrDefId + ";" + label + ";" + captureTime + ";" + value + ";" + "" + ";" + unit;
//	}	
	
}
