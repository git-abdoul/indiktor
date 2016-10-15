package com.fsi.monitoring.dashboard.component.chart;



public class IkrDefinitionChartComponent {// extends IkrChartComponent {	
//	private static final long serialVersionUID = -5916188355216445354L;		
//	private static final Logger logger = Logger.getLogger(IkrDefinitionChartComponent.class);
//	
//	private ConcurrentHashMap<Long, List<IkrValueBean>> ikrDefinitionValues;
//	private Map<Long, Date> staticDataDefEndTimes;
//	
//	public IkrDefinitionChartComponent(String componentId,
//								  	   String title,
//								  	   String style,
//								  	   int width,
//								  	   int height,
//								  	   int maxSlot,
//								  	   String type) {
//		super(componentId, title, style, "definitionChart", width, height, maxSlot, IkrChartComponent.CHART_TIMESERIES);        
//		ikrDefinitionValues = new ConcurrentHashMap<Long, List<IkrValueBean>>();
//		staticDataDefEndTimes = new HashMap<Long, Date>();
//	}	
//	
//	public void setInfo(AbstractIkrDefinition ikrDefinition, IkrCategory ikrCat, String label, DataModelPM dataModelPM) {	
//		ikrIds.add(ikrDefinition.getId());
//		
//		if (label!=null && label.length() != 0) {
//			chartLabels.put(ikrDefinition.getId(), label);
//		} else {
//			chartLabels.put(ikrDefinition.getId(), ikrCat.getLabel()+ ":" +ikrDefinition.getFullIkrInstance());
//		}
//		
//		boolean showStaticData = false;
//		
//		try {
//			if (MetricCompute.STATIC.equals(ikrDefinition.getIkrCompute())) {
//				IkrCategory metricCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrDefinition.getIkrCategoryId());
//				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricCategory.getParentDomainId());
//				StaticDataDefinitionBean sdBean = new StaticDataDefinitionBean(ikrDefinition, metricDomain, metricCategory);				
//				
//				Date captureTime = new Date();
//				IkrValue fromIkrValue = new IkrValue();
//				fromIkrValue.setCaptureTime(captureTime);
//				fromIkrValue.setIkrCategoryId(metricCategory.getId());
//				fromIkrValue.setIkrDefinitionId(ikrDefinition.getId());
//				fromIkrValue.setValue(((StaticData)ikrDefinition).getValue());
//				IkrValueBean fromIkrValueBean = new IkrValueBean(sdBean, fromIkrValue);
//				
//				IkrValue endIkrValue = new IkrValue();
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(captureTime);
//				cal.add(Calendar.SECOND, getMaxSlot());
//				endIkrValue.setCaptureTime(cal.getTime());
//				endIkrValue.setIkrCategoryId(metricCategory.getId());
//				endIkrValue.setIkrDefinitionId(ikrDefinition.getId());
//				endIkrValue.setValue(((StaticData)ikrDefinition).getValue());
//				IkrValueBean endIkrValueBean = new IkrValueBean(sdBean, endIkrValue);
//				List<IkrValueBean> values = new ArrayList<IkrValueBean>();
//				values.add(fromIkrValueBean);
//				values.add(endIkrValueBean);	
//				
//				staticDataDefEndTimes.put(ikrDefinition.getId(), cal.getTime());
//				
//				formats.put(ikrDefinition.getId(), fromIkrValueBean.getFormattedValue());
//				
//				ikrDefinitionValues.put(ikrDefinition.getId(), values);
//				showStaticData = true;
//			}
//		} catch (PersistenceException e) {
//			logger.error(e.getMessage(), e);
//		}		
//		
//		if (showStaticData)
//			computeComponent();
//	}
//
//	@Override
//	protected boolean isAccepted(IkrValueBean ikrValueBean) {
//		long ikrDefinitionId = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
//		return ikrIds.contains(ikrDefinitionId);
//	}
//	
//	public void push(RealTimeBean valueBean) {		
//		IkrValueBean ikrValueBean = (IkrValueBean)valueBean;			
//		if (isAccepted(ikrValueBean)) {
//			long ikrDef = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
//			FormattedValue format = ikrValueBean.getFormattedValue();
//			FormattedValue oldFormat = formats.get(ikrDef);
//			if (oldFormat == null)
//				formats.put(ikrDef, format);
//			else {
//				String oldUnit = oldFormat.getIkrUnit().getSymbol();
//				double oldDivider = oldFormat.getIkrUnit().getDivider().doubleValue();
//				String unit = format.getIkrUnit().getSymbol();				
//				double divider = format.getIkrUnit().getDivider().doubleValue();
//				if (!oldUnit.equals(unit) && divider<oldDivider) {
//					formats.put(ikrDef, format);		
//				}
//			}
//			synchronized (ikrDefinitionValues) {
//				List<IkrValueBean> values = ikrDefinitionValues.get(ikrDef);
//				if(values == null) {
//					values = new ArrayList<IkrValueBean>();
//					ikrDefinitionValues.put(ikrDef, values);
//				}			
//				if (values.size() == MAX_CAPACITY)
//					values.remove(0);
//				values.add(ikrValueBean);
//			}
//		}
//	}	
//
//	public void computeComponent() {
//		int i = 1;
//		int len = ikrDefinitionValues.size();
//		chartData = "";
//		for (long ikrDefId : ikrDefinitionValues.keySet()) {			
//			chartData = chartData + getFlux(ikrDefinitionValues.get(ikrDefId));
//			if(i < len)
//				chartData = chartData + "#FINDTS#";
//			i = i + 1;
//		}
//	}
//
//	@Override
//	protected String buildData(IkrValueBean bean) {
//		long ikrDefId = bean.getIkrDefinitionBean().getIkrDefinition().getId();
//		long captureTime = bean.getIkrValue().getCaptureTime().getTime();
//		if (MetricCompute.STATIC.equals(bean.getIkrDefinitionBean().getIkrDefinition().getIkrCompute())){
//			long end = staticDataDefEndTimes.get(ikrDefId).getTime();
//			long now = (new Date()).getTime();
//			long diff = now - end;
//			if (diff>0) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTime( bean.getIkrValue().getCaptureTime());
//				cal.add(Calendar.MILLISECOND, (int)diff);
//				captureTime = cal.getTime().getTime();
//			}
//		}
//		IkrUnit toUnit = formats.get(ikrDefId).getIkrUnit();
//		String toUnitSymbol = formats.get(ikrDefId).getIkrUnit().getSymbol();
//		FormattedValue formattedValue = bean.getFormattedValue();
//		IkrUnit unit = formattedValue.getIkrUnit();
//		String unitSymbol = formattedValue.getIkrUnit().getSymbol();
//		String value = formattedValue.getValue();
//		if (!toUnitSymbol.equals(unitSymbol)) {
//			value = convertValue(value, unit, toUnit);				
//		}						
//		return ikrDefId+";"+captureTime+";"+value;
//	}	
	
}
