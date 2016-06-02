/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.db;

import be.ac.vub.wise.cmtserver.blocks.Action;
import be.ac.vub.wise.cmtserver.blocks.Binding;
import be.ac.vub.wise.cmtserver.blocks.BindingInputFact;
import be.ac.vub.wise.cmtserver.blocks.BindingInputField;
import be.ac.vub.wise.cmtserver.blocks.BindingIF;
import be.ac.vub.wise.cmtserver.blocks.BindingOutput;
import be.ac.vub.wise.cmtserver.blocks.BindingParameter;
import be.ac.vub.wise.cmtserver.blocks.CMTField;
import be.ac.vub.wise.cmtserver.blocks.CMTParameter;
import be.ac.vub.wise.cmtserver.blocks.EventInput;
import be.ac.vub.wise.cmtserver.blocks.Fact;
import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.blocks.FieldValueLimitation;
import be.ac.vub.wise.cmtserver.blocks.Function;
import be.ac.vub.wise.cmtserver.blocks.IFBlock;
import be.ac.vub.wise.cmtserver.blocks.IFactType;
import be.ac.vub.wise.cmtserver.blocks.Operator;
import be.ac.vub.wise.cmtserver.blocks.OutputHA;
import be.ac.vub.wise.cmtserver.blocks.Rule;
import be.ac.vub.wise.cmtserver.blocks.Template;
import be.ac.vub.wise.cmtserver.blocks.TemplateActions;
import be.ac.vub.wise.cmtserver.blocks.TemplateHA;
import static be.ac.vub.wise.cmtserver.db.DatabaseSQLConnector.ctx;
import static be.ac.vub.wise.cmtserver.db.DatabaseSQLConnector.ds;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Sandra
 */
public class DatabaseSQL implements IDbComponent{

    private static DatabaseSQL comp= null;
    static InitialContext ctx = null;
    static DataSource ds = null;
    
    private DatabaseSQL(){
        try {
            ctx = new InitialContext();
            ds = (DataSource)ctx.lookup("jdbc/myCmtData");
            
        } catch (NamingException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public static DatabaseSQL getDbComponent(){
		if(comp == null){
			comp = new DatabaseSQL();
		}
		return comp;
	}
    
    @Override
    public HashSet<Template> getAvailableContextForms() { 
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addContextForm(Template form) {
        try {
            
            // check if category exists
            try ( // if side
                    Connection conn = ds.getConnection()) {
                // check if category exists
                PreparedStatement ps = conn.prepareStatement("SELECT idtemplate_categories FROM template_categories WHERE idtemplate_categories = ? ");
                ps.setString(1, form.getCategory());
                ResultSet rs = ps.executeQuery();
                ps.closeOnCompletion();
                if(rs.next() || form.getCategory().isEmpty()){ // category empty
                    ps = conn.prepareStatement("INSERT INTO template (template_name, template_category, isSituationTemplate) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, form.getName());
                    if(form.getCategory().isEmpty()){
                        ps.setString(2, "Default");
                    }else{
                        ps.setString(2, form.getCategory());
                    }
                    ps.setInt(3,(form instanceof TemplateHA) ? 1:0 );
                    ps.executeUpdate();
                    ResultSet rs2 = ps.getGeneratedKeys(); ps.closeOnCompletion();
                    
                    rs2.next();
                    int tempId = rs2.getInt(1);
                    rs2.close();
               
                    for(int i = 0; i < form.getIfBlocks().size(); i++){
                        IFBlock ifblock = form.getIfBlocks().get(i);
                        
                        if(ifblock.getFunction() != null){
                            System.out.println("-------------- temp id " + ifblock.getFunction().getSql_id());
                            ps = conn.prepareStatement("INSERT INTO ifblock_function (template_position, function_id, template_id, operator) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, i);
                            ps.setInt(2, ifblock.getFunction().getSql_id());
                            ps.setInt(3, tempId);
                            ps.setString(4, form.getOperators().get(i));
                            ps.executeUpdate();
                            ResultSet rs3 = ps.getGeneratedKeys();
                             ps.closeOnCompletion();
                            rs3.next();
                            int ifblId = rs3.getInt(1);
                            rs3.close();
                      
                            for(Binding binding : ifblock.getBindings()){
                                IFactType ifacttype = null;
                                int fieldId = -1;
                                int indexInput = 0;
                                if(binding.getEndBinding() instanceof BindingInputFact){
                                    ifacttype =((BindingInputFact) binding.getEndBinding()).getInputObject();
                                    indexInput = ((BindingInputFact) binding.getEndBinding()).getIndexObj();
                                }else{
                                    if(binding.getEndBinding() instanceof BindingInputField){
                                        ifacttype =((BindingInputField) binding.getEndBinding()).getInputObject();
                                        indexInput = ((BindingInputField) binding.getEndBinding()).getIndexObj();
                                        fieldId = ((BindingInputField) binding.getEndBinding()).getField().getSql_id();
                                    }
                                }
                                int parameterId = -1;
                                for(CMTParameter pars : ifblock.getFunction().getParameters()){
                                    if(pars.getParName().equals(((BindingIF)binding.getStartBinding()).getIfParameter())){
                                        parameterId = pars.getSql_id();
                                    }
                                }
                                
                                if(ifacttype != null && parameterId != -1){
                                    
                                    
                                    if(ifacttype instanceof FactType || ifacttype instanceof EventInput){
                                        String classname = "";
                                        if(ifacttype instanceof FactType){
                                            classname = ((FactType) ifacttype).getClassName();
                                        }else{
                                            classname = ((EventInput) ifacttype).getClassName();
                                        }
                                        String query ="INSERT INTO ifblock_function_filledin_facttype VALUES (?,?,?,?,?)";
                                        if(fieldId == -1){
                                            query= "INSERT INTO ifblock_function_filledin_facttype(ifblock_id, parameter_id, facttype_id, index_input) VALUES (?,?,?,?)";
                                            ps = conn.prepareStatement(query);
                                            ps.setInt(1, ifblId);
                                            ps.setInt(2, parameterId);
                                            ps.setString(3, classname);
                                            ps.setInt(4, indexInput);
                                            ps.executeUpdate();
                                            ps.close();
                                        }else{
                                            ps = conn.prepareStatement(query);
                                            ps.setInt(1, ifblId);
                                            ps.setInt(2, parameterId);
                                            ps.setString(3, classname);
                                            ps.setInt(4, fieldId);
                                            ps.setInt(5, indexInput);
                                            ps.executeUpdate();
                                            ps.close();
                                        }
                                    }else{
                                        if(ifacttype instanceof Fact){
                                            String query ="INSERT INTO ifblock_function_filledin_fact VALUES (?,?,?,?,?)";
                                            if(fieldId == -1){
                                                query= "INSERT INTO ifblock_function_filledin_fact(ifblock_id, parameter_id, fact_id, index_input) VALUES (?,?,?,?)";
                                                ps = conn.prepareStatement(query);
                                                ps.setInt(1, ifblId);
                                                ps.setInt(2, parameterId);
                                                ps.setInt(3, ((Fact)ifacttype).getId());
                                                ps.setInt(4, indexInput);
                                                ps.executeUpdate();
                                                ps.close();
                                            }else{
                                                ps = conn.prepareStatement(query);
                                                ps.setInt(1, ifblId);
                                                ps.setInt(2, parameterId);
                                                ps.setInt(3, ((Fact)ifacttype).getId());
                                                ps.setInt(4, fieldId);
                                                ps.setInt(5, indexInput);
                                                ps.executeUpdate();
                                                ps.close();
                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            if(ifblock.getEvent()!=null){
                                EventInput eventInput = ((EventInput) ((BindingInputFact)ifblock.getBindings().get(0).getEndBinding()).getInputObject());
                                ps = conn.prepareStatement("INSERT INTO ifblock_event (template_position, facttype_event, template_id, index_input, operator) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                                ps.setInt(1, i);
                                ps.setString(2, ifblock.getEvent().getClassName());
                                ps.setInt(3, tempId);
                                ps.setInt(4,ifblock.getBindings().get(0).getEndBinding().getIndexObj());
                                ps.setString(5, form.getOperators().get(i));
                                ps.executeUpdate();
                                ResultSet rs3 = ps.getGeneratedKeys();
                                rs3.next();
                                int ifblId = rs3.getInt(1);
                                rs3.close();
                                ps.close();
                                // event input als end binding
                                
                                for(FieldValueLimitation  lim : eventInput.getLimitations()){
                                    // lookup field id
                                    int fieldIdLim = -1;
                                    for(CMTField f : eventInput.getFields()){
                                        if(f.getName().equals(lim.getFieldName())){
                                            fieldIdLim = f.getSql_id();
                                        }
                                    }
                                    if(fieldIdLim != -1 && !lim.getValue().isEmpty()){
                                        ps= conn.prepareStatement("INSERT INTO limits_template_ifblock_event (template_id, ifblock_event_id, field_id, operator, value) VALUES(?,?,?,?,?)");
                                        ps.setInt(1, tempId);
                                        ps.setInt(2, ifblId);
                                        ps.setInt(3, fieldIdLim);
                                        ps.setString(4, lim.getOperator());
                                        ps.setString(5, lim.getValue());
                                        ps.executeUpdate();
                                        ps.close();
                                    }
                                }
                            }        
                        }
                    }
                    
                    if(form instanceof TemplateHA){
                        TemplateHA temp = (TemplateHA) form;
                        ps= conn.prepareStatement("INSERT INTO template_situation_output VALUES(?,?)");
                        ps.setInt(1, tempId);
                        ps.setString(2, temp.getOutput().getName());
                        ps.executeUpdate();
                        ps.close();
                        for(Binding binding : temp.getOutput().getBindings()){
                            BindingParameter startBind = binding.getStartBinding();
                            String fieldName = ((BindingOutput)binding.getEndBinding()).getParameter();
                            if(startBind instanceof BindingInputFact){
                                BindingInputFact input = (BindingInputFact) startBind;
                                if(input.getInputObject() instanceof Fact){
                                    ps = conn.prepareStatement("INSERT INTO template_situation_output_fields (template_id, output_field_label, fact_id, facttypeField, index_input) VALUES (?,?,?,?,?)");
                                    ps.setInt(1, tempId);
                                    ps.setString(2, fieldName);
                                    ps.setInt(3, ((Fact)  input.getInputObject()).getId());
                                    ps.setString(4, ((Fact)  input.getInputObject()).getClassName());
                                    ps.setInt(5, input.getIndexObj());
                                    ps.executeUpdate();
                                    ps.close();
                                }else{
                                    String facttypeName = "";
                                    if(input.getInputObject() instanceof FactType){
                                        facttypeName = ((FactType) input.getInputObject()).getClassName();
                                    }else{
                                        if(input.getInputObject() instanceof EventInput){
                                            facttypeName = ((EventInput) input.getInputObject()).getClassName();
                                        }
                                    }
                                    ps = conn.prepareStatement("INSERT INTO template_situation_output_fields (template_id, output_field_label, facttype_name, facttypeField, index_input) VALUES (?,?,?,?,?)");
                                    ps.setInt(1, tempId);
                                    ps.setString(2, fieldName);
                                    ps.setString(3, facttypeName);
                                      ps.setString(4, facttypeName);
                                      ps.setInt(5, input.getIndexObj());
                                    ps.executeUpdate();
                                    ps.close();
                                }
                            }else{
                                if(startBind instanceof BindingInputField && ((BindingInputField)startBind).getInputObject() instanceof Fact){
                                    CMTField field = ((BindingInputField) startBind).getField();
                                    ps = conn.prepareStatement("INSERT INTO template_situation_output_fields (template_id, output_field_label, field_id, fact_id, facttypeField, index_input) VALUES (?,?,?,?,?,?)");
                                    ps.setInt(1, tempId);
                                    ps.setString(2, fieldName);
                                    ps.setInt(3, field.getSql_id());
                                    ps.setInt(4, ((Fact)((BindingInputField)startBind).getInputObject()).getId());
                                      ps.setString(5,  ((Fact)((BindingInputField)startBind).getInputObject()).getClassName());
                                      ps.setInt(6, startBind.getIndexObj());
                                    ps.executeUpdate();
                                    ps.close();
                                }else{
                                    if(startBind instanceof BindingInputField && !(((BindingInputField)startBind).getInputObject() instanceof Fact)){
                                        CMTField field = ((BindingInputField) startBind).getField();
                                        String inputObjectClass = "";
                                        BindingInputField bi = (BindingInputField) startBind;
                                        if(bi.getInputObject() instanceof FactType){
                                            inputObjectClass = ((FactType) bi.getInputObject()).getClassName();
                                        }else{
                                            inputObjectClass = ((EventInput) bi.getInputObject()).getClassName();
                                        }
                                        ps = conn.prepareStatement("INSERT INTO template_situation_output_fields (template_id, output_field_label, field_id, facttype_name, facttypeField, index_input) VALUES (?,?,?,?, ?,?)");
                                        ps.setInt(1, tempId);
                                        ps.setString(2, fieldName);
                                        ps.setInt(3, field.getSql_id());
                                        ps.setString(4, inputObjectClass);
                                           ps.setString(5, field.getType());
                                           ps.setInt(6, startBind.getIndexObj());
                                        ps.executeUpdate();
                                        ps.close();
                                    }
                                }
                            }
                        }
                    }
                    rs.close();
                   
                    conn.close();
                    return true;
                }
                rs.close();
               
                conn.close();
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    @Override
    public boolean removeContextForm(Template form) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashSet<TemplateHA> getAvailableTemplateHA() {
        HashSet<TemplateHA> result = new HashSet<>();
        try {
            try (Connection conn = ds.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM template WHERE isSituationTemplate = 1");
                ResultSet rs = ps.executeQuery();
                 ps.closeOnCompletion();
                while(rs.next()){
                    TemplateHA temp = new TemplateHA();
                    temp.setName(rs.getString("template_name"));
                    temp.setCategory(rs.getString("template_category"));
                    int idTemp = rs.getInt("idtemplate");
                    temp.setSql_id(idTemp);
                    temp.setOutput(getTemplateOutput(idTemp));
                    result.add((TemplateHA)fillLeftSide(temp));
                }
                rs.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return result;
    }
    public EventInput getEventInput(int tempId, int indexInput, FactType type){
        
        try {
            EventInput evInput;
            try (Connection conn = ds.getConnection()) {
                evInput = new EventInput();
                evInput.setClassName(type.getClassName());
                evInput.setFields(type.getFields());
                ArrayList<FieldValueLimitation> lims = new ArrayList<>();
                // query for limits in this template!
                PreparedStatement ps= conn.prepareStatement("SELECT limits_template_ifblock_event.*, fields.fieldName FROM template_situation_output_fields "
                        
                        + " INNER JOIN ifblock_event ON template_situation_output_fields.template_id = ifblock_event.template_id "
                        + " INNER JOIN limits_template_ifblock_event ON limits_template_ifblock_event.ifblock_event_id = ifblock_event.idifblock_event "
                        + " INNER JOIN fields ON limits_template_ifblock_event.field_id = fields.idfields "
                        + " WHERE template_situation_output_fields.template_id = ? AND ifblock_event.index_input = ?");
                ps.setInt(1,tempId);
                ps.setInt(2, indexInput);
                 ps.closeOnCompletion();
                ResultSet rs4 = ps.executeQuery();
                while(rs4.next()){
                    FieldValueLimitation lim = new FieldValueLimitation();
                    lim.setFieldName(rs4.getString("fieldName"));
                    lim.setOperator(rs4.getString("operator"));
                    lim.setValue(rs4.getString("value"));
                    lims.add(lim);
                }
                rs4.close();
          
                    conn.close();
                evInput.setLimitations(lims);
            }
            return evInput;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return null;
    }
    
    private OutputHA getTemplateOutput(int tempId){
        try {
            OutputHA output = new OutputHA();
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT situ_name FROM template_situation_output WHERE template_id = ?");
            ps.setInt(1, tempId);
            ResultSet rs2 = ps.executeQuery();
            rs2.next();
            output.setName(rs2.getString("situ_name"));
            rs2.close();
            ps.closeOnCompletion();
            ps = conn.prepareStatement("SELECT template_situation_output_fields.* FROM template_situation_output_fields "
                    + " WHERE template_id = ?");
            ps.setInt(1, tempId);
            ResultSet rs3 = ps.executeQuery();
            ps.closeOnCompletion();
            LinkedList<Binding> bindings = new LinkedList<>();
            while (rs3.next()){
                Binding binding = new Binding();
                BindingOutput bindOutput = new BindingOutput();
                bindOutput.setIndexObj(0);
                bindOutput.setOutputObj(output);
                bindOutput.setParType(rs3.getString("facttypeField"));
                bindOutput.setParameter(rs3.getString("output_field_label"));
                binding.setEndBinding(bindOutput);
                // get input object! Fact, FactType or EventInput
                IFactType inputObject = null;
                rs3.getString("facttype_name");
                if(!rs3.wasNull()){
                    FactType type= getFactTypeWithName(rs3.getString("facttype_name"));
                    for(CMTField fi : type.getFields()){
                        System.out.println(" --- cmtfield " + fi.getSql_id() );
                    }
                    if(type.getType().equals("activity")){
                        EventInput evInput = getEventInput(tempId, rs3.getInt("index_input"), type);
                        inputObject = evInput;
                    }else{
                        inputObject = type;
                    }
                    
                }else{ // dan ist fact
                    Fact fact = getFact(rs3.getInt("fact_id"));
                    inputObject = fact;
                }
                
                rs3.getInt("field_id");
                if(!rs3.wasNull()){
                    BindingInputField fieldInput = new BindingInputField();
                    ArrayList<CMTField> fields = null;
                    if(inputObject instanceof Fact){
                        fieldInput.setFactId(((Fact)inputObject).getUriValue());
                        fields = ((Fact)inputObject).getFields();
                    }else{
                        if(inputObject instanceof FactType){
                            fields = ((FactType)inputObject).getFields();
                        }else{
                            if(inputObject instanceof EventInput){
                                fields = ((EventInput)inputObject).getFields();
                            }
                        }
                    }
                    for(CMTField f : fields){
                        System.out.println("------- field " + f.getSql_id());
                        System.out.println("------- field db  " +rs3.getInt("field_id") );
                        if(f.getSql_id() == rs3.getInt("field_id")){
                            fieldInput.setField(f);  // niet gezet!!
                            break;
                        }
                    }
                    fieldInput.setIndexObj(rs3.getInt("index_input"));
                    fieldInput.setInputObject(inputObject);
                    
                    binding.setStartBinding(fieldInput);
                }else{
                    BindingInputFact factInput = new BindingInputFact();
                    if(inputObject instanceof Fact){
                        factInput.setFactId(((Fact)inputObject).getUriValue());
                    }
                    factInput.setIndexObj(rs3.getInt("index_input"));
                    factInput.setInputObject(inputObject);
                    binding.setStartBinding(factInput);
                }
                bindings.add(binding);
                
            }
            rs3.close();
            
            output.setBindings(bindings);
            return output;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    
    }
    
    private Template fillLeftSide(Template temp){
        try {
            // get all event if blocks
            // get all function ifblocks
            // sort by asc
            HashMap<Integer, IFBlock> blocks = new HashMap<>();
            HashMap<Integer, String> operators = new HashMap<>();
            try (Connection conn = ds.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM ifblock_event WHERE template_id = ?");
                ps.setInt(1, temp.getSql_id());
                ResultSet rs = ps.executeQuery();
                 ps.closeOnCompletion();
                while(rs.next()){
                    // create ifblock of event
                    IFBlock ifblo = new IFBlock();
                    FactType event = getFactTypeWithName(rs.getString("facttype_event"));
                    ifblo.setEvent(event);
                    ifblo.setType("activity");
                    // add bindings -- enkel binding met zichzelf met limits ingevuld
                    //
                    Binding binding = new Binding();
                    BindingIF startB = new BindingIF();
                    startB.setIfParameter("");
                    startB.setIndexObj(rs.getInt("template_position"));
                    binding.setStartBinding(startB);
                    BindingInputFact input = new BindingInputFact();
                    input.setIndexObj(rs.getInt("index_input"));
                    EventInput evInput = new EventInput();
                    evInput.setClassName(event.getClassName());
                    evInput.setFields(event.getFields());
                    ArrayList<FieldValueLimitation> lims = new ArrayList<>();
                    // query for limits in this template!
                    ps= conn.prepareStatement("SELECT limits_template_ifblock_event.*, fields.fieldName FROM ifblock_event "
                            
                          
                            + " INNER JOIN limits_template_ifblock_event ON limits_template_ifblock_event.ifblock_event_id = ifblock_event.idifblock_event "
                            + " INNER JOIN fields ON limits_template_ifblock_event.field_id = fields.idfields "
                            + " WHERE ifblock_event.template_id = ? AND ifblock_event.template_position = ?");
                    
                    
                    ps.setInt(1,temp.getSql_id());
                    ps.setInt(2, rs.getInt("template_position"));
                    ResultSet rs4 = ps.executeQuery();
                    while(rs4.next()){
                        
                        FieldValueLimitation lim = new FieldValueLimitation();
                        lim.setFieldName(rs4.getString("fieldName"));
                        lim.setOperator(rs4.getString("operator"));
                        lim.setValue(rs4.getString("value"));
                        lims.add(lim);
                    }
                    rs4.close();
                    ps.close();
                    evInput.setLimitations(lims);
                    input.setInputObject(evInput);
                    binding.setEndBinding(input);
                    ifblo.addBinding(binding);
                    blocks.put(rs.getInt("template_position"), ifblo);
                    operators.put(rs.getInt("template_position"), rs.getString("operator"));
                }
                rs.close();
               
                ps = conn.prepareStatement("SELECT * FROM ifblock_function WHERE template_id = ?");
                ps.setInt(1, temp.getSql_id());
                ResultSet rs2 = ps.executeQuery();
                 ps.closeOnCompletion();
                while(rs2.next()){
                    // create ifblock of functions
                    IFBlock ifblo = new IFBlock();
                    Function func = getFunction(rs2.getInt("function_id"));
                    ifblo.setFunction(func);
                    ifblo.setType("function");
                    // add bindings
                    // get parameters dan voor alle parameters een binding
                    for(CMTParameter par : func.getParameters()){
                        // start binding
                        Binding binding = new Binding();
                        BindingIF start = new BindingIF();
                        start.setIfParameter(par.getParName());
                        start.setIndexObj(rs2.getInt("template_position"));
                        // endbinding is factype of fact
                        binding.setStartBinding(start);
                        ps = conn.prepareStatement("SELECT * FROM ifblock_function_filledIn_fact WHERE ifblock_id = ? AND parameter_id = ?");
                        ps.setInt(1, rs2.getInt("idifblock_function"));
                        ps.setInt(2, par.getSql_id());
                        ResultSet rs4 = ps.executeQuery();
                         ps.closeOnCompletion();
                        while(rs4.next()){
                            Fact fact = getFact(rs4.getInt("fact_id"));
                            rs4.getInt("field_id");
                            if(!rs4.wasNull()){
                                int fieldId = rs4.getInt("field_id");
                                BindingInputField endBind = new BindingInputField();
                                endBind.setInputObject(fact);
                                endBind.setIndexObj(rs4.getInt("index_input"));
                                for(CMTField f : fact.getFields()){
                                    if(f.getSql_id() == fieldId){
                                        endBind.setField(f);
                                        break;
                                    }
                                }
                                binding.setEndBinding(endBind);
                            }else{
                                BindingInputFact endBind = new BindingInputFact();
                                endBind.setInputObject(fact);
                                endBind.setIndexObj(rs4.getInt("index_input"));
                                binding.setEndBinding(endBind);
                            }
                        }
                        rs4.close();
                      
                        if(binding.getEndBinding() == null){
                            System.out.println("------------- in goede");
                            ps = conn.prepareStatement("SELECT ifblock_function_filledIn_facttype.* , facttype.facttypeType FROM ifblock_function_filledIn_facttype "
                                    + "INNER JOIN facttype ON ifblock_function_filledIn_facttype.facttype_id = facttype.facttypeName "
                                    + "WHERE ifblock_id = ? AND parameter_id = ? ");
                            ps.setInt(1, rs2.getInt("idifblock_function"));
                            ps.setInt(2, par.getSql_id());
                             ps.closeOnCompletion();
                            ResultSet rs3 = ps.executeQuery();
                            while(rs3.next()){
                                
                                System.out.println(" ----------- in rs3 ");
                                
                                // check if its event
                                FactType type = getFactTypeWithName(rs3.getString("facttype_id"));
                                IFactType input = null;
                                if(rs3.getString("facttypeType").equals("activity")){
                                    // create EventInput
                                    EventInput event = getEventInput(temp.getSql_id(), rs3.getInt("index_input") , type);
                                    input = event;
                                    
                                }else{
                                    input = type;
                                }
                                rs3.getInt("field_id");
                                if(!rs3.wasNull()){
                                    int fieldId = rs3.getInt("field_id");
                                    BindingInputField endBind = new BindingInputField();
                                    endBind.setInputObject(input);
                                    endBind.setIndexObj(rs3.getInt("index_input"));
                                    for(CMTField f : type.getFields()){
                                        if(f.getSql_id() == fieldId){
                                            endBind.setField(f);
                                            break;
                                        }
                                    }
                                    binding.setEndBinding(endBind);
                                }else{
                                    System.out.println("------- in fact bind");
                                    BindingInputFact endBind = new BindingInputFact();
                                    endBind.setInputObject(input);
                                    endBind.setIndexObj(rs3.getInt("index_input"));
                                    binding.setEndBinding(endBind);
                                }
                            }
                            rs3.close();
                            
                        }
                        
                        ifblo.addBinding(binding);
                    }
                    blocks.put(rs2.getInt("template_position"), ifblo);
                    operators.put(rs2.getInt("template_position"), rs2.getString("operator"));
                   
                    
                }
                 rs2.close();
                
                for(int i = 0; i<blocks.keySet().size();i++){
                    Operator op = new Operator();
                    op.setOperator(operators.get(i));
                    temp.addIfBlock(blocks.get(i),op);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return temp;
    }
    
    

    @Override
    public HashSet<TemplateActions> getAvailableTemplateActions() {
        HashSet<TemplateActions> result = new HashSet<>();
        
        return result;
        
    }

    @Override
    public HashSet<IFactType> getFacts() { // not in sql
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     @Override
    public HashSet<Fact> getFactsInFactVersions() {
        HashSet<Fact> result = new HashSet<Fact>();
       try {
            Connection conn = ds.getConnection();
            Statement ps = conn.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * from facts");
            while(rs.next()){
                Fact fact =  getFactFields(rs.getInt("idfacts"));
                fact.setId(rs.getInt("idfacts"));
                fact.setClassName(rs.getString("facttype"));
                result.add(fact);
            }
            rs.close();
               ps.close();
               conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private Fact getFactFields(int factId){
        try {
            Fact fact = new Fact(null,null);
            Connection conn2 = ds.getConnection();
            // string fields
            PreparedStatement ps2 = conn2.prepareStatement("select facttype.*, fact_field_values_string.value, fields.* from facts " +
                    "inner join fact_field_values_string on facts.idfacts = fact_field_values_string.fact_id " +
                    "inner join fields on fact_field_values_string.field_id = fields.idfields " +
                    "inner join facttype on facttype.facttypeName = facts.facttype " +
                    "where facts.idfacts = ?");
            ps2.setInt(1, factId);
            ResultSet rs = ps2.executeQuery();
             ps2.closeOnCompletion();
            ArrayList<CMTField> fields = new ArrayList<>();
            String className = "";
            while(rs.next()){
                className = rs.getString("facttypeName");
                CMTField field = new CMTField(rs.getString("fieldName"), rs.getString("fieldType")); // set limits enz!!
                field.setIsVar(rs.getInt("isVar")!=0);
                field.setSql_id(rs.getInt("idfields"));
                if(rs.getInt("idfields") == rs.getInt("urifield")){
                    fact.setUriField(rs.getString("fieldName"));
                }
                field.setValue(rs.getString("value"));
                fields.add(field);
                
            }
            rs.close();
            
            ps2 = conn2.prepareStatement("select facttype.*, fact_field_values.value, fields.* from facts " +
                    "inner join fact_field_values on facts.idfacts = fact_field_values.factId " +
                    "inner join fields on fact_field_values.fieldId = fields.idfields " +
                    "inner join facttype on facttype.facttypeName = facts.facttype " +
                    "where facts.idfacts = ?");
            ps2.setInt(1, factId);
            rs = ps2.executeQuery();
             ps2.closeOnCompletion();
            while(rs.next()){
                className = rs.getString("facttypeName");
                CMTField field = new CMTField(rs.getString("fieldName"), rs.getString("fieldType")); // set limits enz!!
                field.setIsVar(rs.getInt("isVar")!=0);
                field.setSql_id(rs.getInt("idfields"));
                if(rs.getInt("idfields") == rs.getInt("urifield")){
                    fact.setUriField(rs.getString("fieldName"));
                }
                field.setValue(getFactFields(rs.getInt("value")));
                fields.add(field);
            }
           rs.close();
           
                
            fact.setFields(fields);
            fact.setId(factId);
            fact.setClassName(className);
            
            conn2.close();
            return fact;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public IFactType getFact(String className, String uriField, String value) { // not in sql!
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Fact getFactInFactForm(String className, String uriField, String value) { 
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT fact_field_values_string.fact_id FROM facttype "
                                                        + " INNER JOIN fact_field_values_string ON facttype.urifield = fact_field_values_string.field_id "
                                                        + " WHERE facttype.facttypeName = ? AND fact_field_values_string.value = ?");
            ps.setString(1, className);
            ps.setString(2, value);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Fact fact = getFact(rs.getInt("fact_id"));
            
            rs.close();
            if(ps != null){
                    ps.close();
                }
            conn.close();
            return fact;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    // get Fact in fact form based on id 
    @Override 
    public Fact getFact(int sqlid){
       return getFactFields(sqlid);
    }
    
    @Override
    public HashSet<FactType> getAvailableFactTypes() {
        HashSet<FactType> result = new HashSet<FactType>();
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * from facttype WHERE facttypeType = 'fact'");
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
            while(rs.next()){
                FactType facttype = new FactType(rs.getString("facttypeName"), rs.getString("facttypeType"), "", new ArrayList<CMTField>()); // type and fields next query
                facttype.setIsCustom(rs.getInt("isCustom")!=0);
                facttype.setCategory(rs.getString("facttypeCategory"));
                result.add(facttype);
            }
            
            rs.close();
           
            for(FactType type : result){
            // get fields
                ps = conn.prepareStatement("select fields.* ,facttype.urifield from facttype " +
                                                                    "inner join  facttype_fields on facttype.facttypeName = facttype_fields.facttype " +
                                                                    "inner join fields on facttype_fields.idfield = fields.idfields " +
                                                                    "where facttype.facttypeName = ?");
                ps.setString(1, type.getClassName());
                ResultSet rs2 = ps.executeQuery();
                 ps.closeOnCompletion();
                while(rs2.next()){
                    CMTField field = new CMTField(rs2.getString("fieldName"), rs2.getString("fieldType")); // set limits enz!!
                    field.setSql_id(rs2.getInt("idfields"));
                    type.addCMTField(field);
                    if(type.getType().equals("fact")){
                        if(rs2.getInt("idfields") == rs2.getInt("urifield")){
                            type.setUrifield(rs2.getString("fieldName"));
                        }
                    }
                } 
         
           
            }
             
                   conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public void registerEventType(FactType type) {
         try {
            Connection conn = ds.getConnection();
            // check if category is ok
            PreparedStatement ps = conn.prepareStatement("SELECT categoryName FROM categories WHERE categoryName = ?"); 
            ps.setString(1, type.getCategory());
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
             System.out.println(" cat " + type.getCategory() );
            if(rs.next()){ // then category exists!
                System.out.println(" in rs next ");
                String category = rs.getString("categoryName");
                ps = conn.prepareStatement("INSERT INTO facttype (facttypeName, facttypeCategory, isCustom, facttypeType) VALUES(?,?,?,?)");
                ps.setString(1, type.getClassName());
                ps.setString(2, category);
                ps.setInt(3, type.isIsCustom() ? 1 : 0);
                ps.setString(4, type.getType());
                // urifield after fields insert!
                ps.executeUpdate();
                ps.close();
                for(CMTField field : type.getFields()){
                    
                    ps = conn.prepareStatement("SELECT facttypeName FROM facttype WHERE facttypeName = ?");
                    ps.setString(1, field.getType());
                    ResultSet rs3 = ps.executeQuery();
                     ps.closeOnCompletion();
                    rs3.next();
                    String typeName = rs3.getString("facttypeName");
                    rs3.close();
                    ps.close();
                    ps = conn.prepareStatement("INSERT INTO fields (fieldName, fieldType, isVar) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, field.getName());
                    ps.setString(2,typeName);
                    ps.setInt(3, field.isIsVar() ? 1:0);
                    ps.executeUpdate();
                    ResultSet rs2 = ps.getGeneratedKeys();
                    rs2.next();
                    int fieldid = rs2.getInt(1);
                    rs2.close();
                    ps.close();
                    ps = conn.prepareStatement("INSERT INTO facttype_fields VALUES(?,?)");
                    ps.setString(1, type.getClassName());
                    ps.setInt(2, fieldid);
                    ps.executeUpdate();
                    ps.close();
                    // limits!
                    if(field.isIsVar()){
                        System.out.println("------------- in is var");
                        if(!field.getFormat().isEmpty()){
                            System.out.println("------------- in is var format");
                            ps = conn.prepareStatement("INSERT INTO event_limits_format (facttype_name, field_id, format) VALUES(? , ? , ?)");
                            ps.setString(1, type.getClassName());
                            ps.setInt(2, fieldid);
                            ps.setString(3, field.getFormat());
                            ps.executeUpdate();
                            ps.close();
                        
                        }else{
                            if(field.getOptions()!=null && !field.getOptions().isEmpty()){
                                ps = conn.prepareStatement("INSERT INTO event_limits_list (facttype_name, field_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                                ps.setString(1, type.getClassName());
                                ps.setInt(2, fieldid);
                                ps.executeUpdate();
                                ResultSet rs5 = ps.getGeneratedKeys();
                                rs5.next();
                                int listid = rs5.getInt(1);
                                rs5.close();
                                ps.close();
                                for(String option : field.getOptions()){
                                    ps = conn.prepareStatement("INSERT INTO event_limits_list_options VALUES(?,?)");
                                    ps.setInt(1, listid);
                                    ps.setString(2, option);
                                    ps.executeUpdate();
                                    ps.close();
                                }
                            }
                        }
                    }
                    
                }
            }else{
                rs.close();
          
                throw new UnsupportedOperationException("Something went wrong"); 
            }
           
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public HashSet<FactType> getAvailableEventTypes() {
                HashSet<FactType> result = new HashSet<FactType>();
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * from facttype WHERE facttypeType = 'activity'");
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
            while(rs.next()){
                FactType facttype = new FactType(rs.getString("facttypeName"), rs.getString("facttypeType"), "", new ArrayList<CMTField>()); // type and fields next query
                facttype.setIsCustom(rs.getInt("isCustom")!=0);
                facttype.setCategory(rs.getString("facttypeCategory"));
                result.add(facttype);
            }
            rs.close();
          
            for(FactType type : result){
            // get fields
                ps = conn.prepareStatement("select fields.* ,facttype.urifield from facttype " +
                                                                    "inner join  facttype_fields on facttype.facttypeName = facttype_fields.facttype " +
                                                                    "inner join fields on facttype_fields.idfield = fields.idfields " +
                                                                    "where facttype.facttypeName = ?");
                ps.setString(1, type.getClassName());
                ResultSet rs2 = ps.executeQuery();
                ps.closeOnCompletion();
                while(rs2.next()){
                    CMTField field = new CMTField(rs2.getString("fieldName"), rs2.getString("fieldType")); // set limits enz!!
                    field.setSql_id(rs2.getInt("idfields"));
                    if(rs2.getInt("isVar")!=0){
                        System.out.println(" --- in get is var");
                        field.setIsVar(rs2.getInt("isVar")!=0);
                        ps = conn.prepareStatement("SELECT format FROM event_limits_format WHERE facttype_name = ? AND field_id = ?");
                        ps.setString(1, type.getClassName());
                        ps.setInt(2, field.getSql_id());
                        ResultSet rs3 = ps.executeQuery();
                         ps.closeOnCompletion();
                        if(rs3.next()){
                            field.setFormat(rs3.getString("format"));
                            rs3.close();
                        }else{
                            ps = conn.prepareStatement("SELECT idevent_limits_list FROM event_limits_list WHERE facttype_name = ? AND field_id = ?");
                            ps.setString(1, type.getClassName());
                            ps.setInt(2, field.getSql_id());
                            ResultSet rs4 = ps.executeQuery();
                             ps.closeOnCompletion();
                            if(rs4.next()){
                                int id_list = rs4.getInt("idevent_limits_list");
                                System.out.println("--------- list id " + id_list);
                                ps = conn.prepareStatement("SELECT option_list FROM event_limits_list_options WHERE id_event_list = ?");
                                ps.setInt(1, id_list);
                                ResultSet rs5 = ps.executeQuery();
                                 ps.closeOnCompletion();
                                while(rs5.next()){
                                    field.addOption(rs5.getString("option_list"));
                                }
                                rs5.close();
                            }
                            rs4.close();
                        }
                    }
                    
                    type.addCMTField(field);
                }
                rs2.close();
                    
            }
             
                   conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public FactType getFactTypeWithName(String name) {
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * from facttype where facttypeName = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
            if(rs.next()){
               
                FactType facttype = new FactType(rs.getString("facttypeName"), rs.getString("facttypeType"), "", new ArrayList<CMTField>()); // type and fields next query
                facttype.setIsCustom(rs.getInt("isCustom")!=0);
                facttype.setCategory(rs.getString("facttypeCategory"));
                ps = conn.prepareStatement("select fields.* ,facttype.urifield from facttype " +
                                                                    "inner join  facttype_fields on facttype.facttypeName = facttype_fields.facttype " +
                                                                    "inner join fields on facttype_fields.idfield = fields.idfields " +
                                                                    "where facttype.facttypeName = ?");
                ps.setString(1, name);
                ResultSet rs2 = ps.executeQuery();
                 ps.closeOnCompletion();
                while(rs2.next()){
                    CMTField field = new CMTField(rs2.getString("fieldName"), rs2.getString("fieldType")); // set limits enz!!
                    field.setIsVar(rs2.getInt("isVar")!=0);
                    field.setSql_id(rs2.getInt("idfields"));
                    facttype.addCMTField(field);
                    if(rs2.getInt("idfields") == rs2.getInt("urifield")){
                        facttype.setUrifield(rs2.getString("fieldName"));
                    }
                    if(field.isIsVar()){
                        // query format or options
                        ps = conn.prepareStatement("SELECT format FROM event_limits_format WHERE field_id = ?");
                        ps.setInt(1, field.getSql_id());
                        ResultSet rs3 = ps.executeQuery();
                         ps.closeOnCompletion();
                        if(rs3.next()){
                            field.setFormat(rs3.getString("format"));
                            rs3.close();
                        }else{
                            ArrayList<String> options = new ArrayList<>();
                            ps = conn.prepareStatement("SELECT event_limits_list_options.option_list FROM event_limits_list "
                                                        + " INNER JOIN event_limits_list_options ON event_limits_list.idevent_limits_list =  event_limits_list_options.id_event_list "
                                                        + " WHERE event_limits_list.field_id = ?");
                            ps.setInt(1, field.getSql_id());
                             ps.closeOnCompletion();
                            ResultSet rs4 = ps.executeQuery();
                            while(rs4.next()){
                                options.add(rs4.getString("option_list"));
                            }
                            rs4.close();
                            field.setOptions(options);
                        }
                    }
                }
                rs.close();
                rs2.close();
                return facttype;
            }
            rs.close();
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }

    @Override
    public HashSet<IFactType> getFactsWithType(String classNamed) { // not in sql!
                  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }
    
    @Override
    public HashSet<Fact> getFactsInFactVersionWithType(String classNamed) {
          HashSet<Fact> result = new HashSet<Fact>();
       try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM facts WHERE facttype = ?");
            ps.setString(1, classNamed);
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
            while(rs.next()){
                Fact fact =  getFactFields(rs.getInt("idfacts"));
                fact.setId(rs.getInt("idfacts"));
                fact.setClassName(rs.getString("facttype"));
                result.add(fact);
            }
               rs.close();
               conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public void registerFactType(FactType type) {
        // insert facttype
        // insert fields 
        // allocate fields to facttype
        
        try {
            Connection conn = ds.getConnection();
            // check if category is ok
            PreparedStatement ps = conn.prepareStatement("SELECT categoryName FROM categories WHERE categoryName = ?"); 
            ps.setString(1, type.getCategory());
            
            System.out.println("1DB>>>>>>>>>>> Category = " + type.getCategory());
            
            ResultSet rs = ps.executeQuery();
             ps.closeOnCompletion();
            if(rs.next()){ // then category exists!
                String category = rs.getString("categoryName");
                ps = conn.prepareStatement("INSERT INTO facttype (facttypeName, facttypeCategory, isCustom, facttypeType) VALUES(?,?,?,?)");
                ps.setString(1, type.getClassName());
                ps.setString(2, category);
                ps.setInt(3, type.isIsCustom() ? 1 : 0);
                ps.setString(4, type.getType());
                // urifield after fields insert!
                ps.executeUpdate();
                ps.close();
                for(CMTField field : type.getFields()){
                    
                    ps = conn.prepareStatement("SELECT facttypeName FROM facttype WHERE facttypeName = ?");
                    ps.setString(1, field.getType());
                    
                    System.out.println("?PM>>>>> " + field.getType());
                    
                            ResultSet rs3 = ps.executeQuery();
                    ps.closeOnCompletion();
                    rs3.next();
                    //LVH
                    ResultSetMetaData metadata = rs.getMetaData();
                    int columnCount = metadata.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println("MCol>>>>" + metadata.getColumnName(i) + ", ");  
                        System.out.println("Index: " + i + " -- Content: " + rs3.getString(i));
                    }
                    System.out.println("NOfColls>>>>" + metadata.getColumnCount());
                    
                    while (rs.next()) {
                        String row = "";
                        for (int i = 1; i <= columnCount; i++) {
                            row += rs.getString(i) + ", ";
                        }
                        System.out.println("Row>>>>" + row);
                    }

                    String typeName = rs3.getString("facttypeName");
                    rs3.close();
                    ps = conn.prepareStatement("INSERT INTO fields (fieldName, fieldType, isVar) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, field.getName());
                    ps.setString(2,typeName);
                    ps.setInt(3, field.isIsVar() ? 1:0);
                    ps.executeUpdate();
               
                    ResultSet rs2 = ps.getGeneratedKeys();
                    rs2.next();
                    int fieldid = rs2.getInt(1);
                    rs2.close();
                         ps.close();
                    if(field.getName().equals(type.getUriField())){
                        ps = conn.prepareStatement("UPDATE facttype SET urifield = ? WHERE facttypeName = ?" );
                        ps.setInt(1, fieldid);
                        ps.setString(2, type.getClassName());
                        ps.executeUpdate();
                             ps.close();
                    }
                    ps = conn.prepareStatement("INSERT INTO facttype_fields VALUES(?,?)");
                    ps.setString(1, type.getClassName());
                    ps.setInt(2, fieldid);
                    ps.executeUpdate();
                         ps.close();
                }
            }else{
                rs.close();
                throw new UnsupportedOperationException("Something went wrong"); 
            }
      
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    @Override
    public void addFact(IFactType fact) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }
    
    @Override
    public void addFactinFactForm(Fact fact) {
        try {
            // add fact
            // add fact field values
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT facttypeName FROM facttype WHERE facttypeName = ?");
            ps.setString(1, fact.getClassName());
            ResultSet rs = ps.executeQuery();
            ps.closeOnCompletion();
            if(rs.next()){
                PreparedStatement ps1 = conn.prepareStatement("INSERT INTO facts (facttype) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, rs.getString("facttypeName"));
                ps1.executeUpdate();
                ResultSet rs2 = ps1.getGeneratedKeys();
                rs2.next();
                int factid = rs2.getInt(1);
                rs2.close();
                ps1.closeOnCompletion();
                for(CMTField field : fact.getFields()){
                    // id field 
                    // recursief fact as value till level of string 
                    if(field.getType().equals("java.lang.String")){
                        // insert value string  
                        
                        ps1 = conn.prepareStatement("INSERT INTO fact_field_values_string VALUES(?,?,?)");
                        ps1.setInt(1, factid);
                        ps1.setInt(2, field.getSql_id());
                        ps1.setString(3, field.getValue().toString());
                        ps1.executeUpdate();
                        ps1.close();
                    }else{
                        if(field.getValue() instanceof Fact){
                            int factidOfValue = ((Fact) field.getValue()).getId();
                            ps1 = conn.prepareStatement("INSERT INTO fact_field_values VALUES(?,?,?)");
                            ps1.setInt(1, factid);
                            ps1.setInt(2, field.getSql_id());
                            ps1.setInt(3, factidOfValue);
                            ps1.executeUpdate();
                            ps1.close();
                        }
                    }     
                }
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    @Override
    public boolean removeFact(IFactType fact) { // not bin sql
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addFunction(Function function) {
        try {
            System.out.println("-------------- in add function" + function.getParameters().size());
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO function (function_name, encap_class) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, function.getName());
            ps.setString(2, function.getEncapClass());
            ps.executeUpdate();
            ps.closeOnCompletion();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int functId = rs.getInt(1);
            rs.close();
            for(CMTParameter par : function.getParameters()){
                ps = conn.prepareStatement("INSERT INTO parameters (function_parameter_name, function_parameter_position, function_parameter_type) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, par.getParName());
                ps.setInt(2, par.getPosition());
                ps.setString(3, par.getType());
                ps.executeUpdate();
                ps.closeOnCompletion();
                ResultSet rs2 = ps.getGeneratedKeys();
                rs2.next();
                int parId = rs2.getInt(1);
                rs.close();
                ps = conn.prepareStatement("INSERT INTO function_parameters VALUES (?,?)");
                ps.setInt(1, functId);
                ps.setInt(2, parId);
                ps.executeUpdate();
                ps.close();
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public Function getFunction (int sqlId){
        try {
                Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM function WHERE idfunction = ?");
                ps.setInt(1, sqlId);
                ResultSet rs = ps.executeQuery();
                ps.closeOnCompletion();
                while(rs.next()){
                    Function func = new Function();
                    func.setName(rs.getString("function_name"));
                    func.setEncapClass(rs.getString("encap_class"));
                    ArrayList<CMTParameter> pars = new ArrayList<>();
                    ps = conn.prepareStatement("SELECT parameters.* FROM parameters"
                                                + " INNER JOIN function_parameters ON parameters.idfunction_parameters = function_parameters.parameter_id "
                                                + " WHERE function_parameters.function_id = ? ");
                    ps.setInt(1, rs.getInt("idfunction"));
                    ResultSet rs2 = ps.executeQuery();
                    ps.closeOnCompletion();
                    while(rs2.next()){
                        CMTParameter par = new CMTParameter();
                        par.setParName(rs2.getString("function_parameter_name"));
                        par.setType(rs2.getString("function_parameter_type"));
                        par.setPosition(rs2.getInt("function_parameter_position"));
                        par.setSql_id(rs2.getInt("idfunction_parameters"));
                        pars.add(par);
                    }
                    rs2.close();
                    func.setSql_id(rs.getInt("idfunction"));
                    func.setParameters(pars);
                    conn.close();
                    return func;
                }
                rs.close();
                conn.close();

            } catch (SQLException ex) {
                Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return null;
    }

    @Override
    public HashSet<Function> getFunctions() {
        HashSet<Function> result = new HashSet<>();
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM function");
            ResultSet rs = ps.executeQuery();
            ps.closeOnCompletion();
            while(rs.next()){
                Function func = new Function();
                func.setName(rs.getString("function_name"));
                func.setEncapClass(rs.getString("encap_class"));
                ArrayList<CMTParameter> pars = new ArrayList<>();
                ps = conn.prepareStatement("SELECT parameters.* FROM parameters"
                                            + " INNER JOIN function_parameters ON parameters.idfunction_parameters = function_parameters.parameter_id "
                                            + " WHERE function_parameters.function_id = ? ");
                ps.setInt(1, rs.getInt("idfunction"));
                ResultSet rs2 = ps.executeQuery();
                ps.closeOnCompletion();
                while(rs2.next()){
                    CMTParameter par = new CMTParameter();
                    par.setParName(rs2.getString("function_parameter_name"));
                    par.setType(rs2.getString("function_parameter_type"));
                    par.setPosition(rs2.getInt("function_parameter_position"));
                    par.setSql_id(rs2.getInt("idfunction_parameters"));
                    pars.add(par);
                }
                rs2.close();
                func.setSql_id(rs.getInt("idfunction"));
                func.setParameters(pars);
                result.add(func);
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return result;
    }

    @Override
    public boolean addAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashSet<Action> getActions() {
        HashSet<Action> result = new HashSet<>();
        return result;
    }

    @Override
    public Action getAction(String className) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addRule(Rule rule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashSet<Rule> getRules() {
        HashSet<Rule> result = new HashSet();
        return result;
    }

    @Override
    public Rule getRule(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    @Override
    public boolean restartDb() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeDb() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void resetDb(){
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("select table_name from information_schema.tables where table_schema=? ");
            ps.setString(1, "cmt");
            ResultSet rs = ps.executeQuery();
            ps.closeOnCompletion();
            while(rs.next()){
                String query = "DELETE FROM  " + rs.getString("table_name");
                ps = conn.prepareStatement(query);
               
                ps.executeUpdate();
            }
           
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void addDefaultCategories(){
        Connection conn;
        try {
            conn = ds.getConnection();
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO `cmt`.`categories` (`categoryName`) VALUES ('d');");
            ps1.executeUpdate();
            ps1.closeOnCompletion();
            
            PreparedStatement ps2 = conn.prepareStatement("INSERT INTO `cmt`.`categories` (`categoryName`) VALUES ('Default');");
            ps2.executeUpdate();
            ps2.closeOnCompletion();
            
            PreparedStatement ps3 = conn.prepareStatement("INSERT INTO `cmt`.`categories` (`categoryName`) VALUES ('Code');");
            ps3.executeUpdate();
            ps3.closeOnCompletion();
            
            conn.close();           
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
   public Template getTemplateOfSituation(String situationName){
        try {
            // get Rule
            // get template of rule
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT rule_id FROM rule_situation WHERE facttype_id = ?");
            ps.setString(1, situationName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int ruleId = rs.getInt("rule_id");
            rs.close();
            ps.close();
            TemplateHA temp = new TemplateHA();
            ps = conn.prepareStatement("SELECT template.* FROM rules "
                                        + " INNER JOIN template ON template.idtemplate = rules.template_id "
                                        + "WHERE rules.idrules = ?");
            ps.setInt(1, ruleId);
            ResultSet rs2 = ps.executeQuery();
            ps.closeOnCompletion();
            rs2.next();
   
            temp.setName(rs2.getString("template_name"));
            temp.setCategory(rs2.getString("template_category"));
            int idTemp = rs2.getInt("idtemplate");
            temp.setSql_id(idTemp);
            temp.setOutput(getTemplateOutput(idTemp));
            temp.getOutput().setName(situationName);
            temp = (TemplateHA) fillLeftSide(temp);
            rs2.close();
            // fill in limitations if ifblock is event or add fact to inputblock
            for(IFBlock ifblock : temp.getIfBlocks()){
                if(ifblock.getFunction() != null){
                    for(Binding binding : ifblock.getBindings()){
                        IFactType inputObject = null;
                        int indexInput = binding.getEndBinding().getIndexObj();
                        if(binding.getEndBinding() instanceof BindingInputFact){
                            inputObject = ((BindingInputFact) binding.getEndBinding()).getInputObject();
                        }else{
                            inputObject = ((BindingInputField) binding.getEndBinding()).getInputObject();
                        }
                        if(inputObject instanceof EventInput){
                            // get limits for rule
                            EventInput event = (EventInput) inputObject;
                            ps = conn.prepareStatement("SELECT * FROM rule_limits_ifblock_event WHERE rule_id = ? AND index_input = ?");
                            ps.setInt(1, ruleId);
                            ps.setInt(2, indexInput);
                            ResultSet rs3 = ps.executeQuery();
                            ps.closeOnCompletion();
                            ArrayList<FieldValueLimitation> lims = new ArrayList<>();
                            while(rs3.next()){
                                FieldValueLimitation lim = new FieldValueLimitation();
                                for(CMTField fi: event.getFields()){
                                    if(fi.isIsVar() && fi.getSql_id() == rs3.getInt("field_id")){
                                        lim.setFieldName(fi.getName());
                                        break;
                                    }
                                }
                                lim.setOperator(rs3.getString("operator"));
                                lim.setValue(rs3.getString("value"));
                                lims.add(lim);
                            }
                            rs3.close();
                            event.setLimitations(lims);
                        }else{
                            if(inputObject instanceof FactType){
                                // get fact
                                ps = conn.prepareStatement("SELECT fact_id FROM rule_param_filledin_fact WHERE rule_id = ? AND index_input = ?");
                                ps.setInt(1, ruleId);
                                ps.setInt(2, indexInput);
                                ResultSet rs5 = ps.executeQuery();
                                rs5.next();
                                Fact fact = getFact(rs5.getInt("fact_id"));
                                if(binding.getEndBinding() instanceof BindingInputFact){
                                    ((BindingInputFact) binding.getEndBinding()).setInputObject(fact);
                                }else{
                                    ((BindingInputField) binding.getEndBinding()).setInputObject(fact);
                                }
                                rs5.close();
                                ps.close();
                            } // else if Fact leave it! is already filled in in template by expert
                        }
                    }
                }else{
                    if(ifblock.getEvent() != null){
                        EventInput event = (EventInput) ((BindingInputFact) ifblock.getBindings().get(0).getEndBinding()).getInputObject();
                        int indexInput = ifblock.getBindings().get(0).getEndBinding().getIndexObj();
                            ps = conn.prepareStatement("SELECT * FROM rule_limits_ifblock_event WHERE rule_id = ? AND index_input = ?");
                            ps.setInt(1, ruleId);
                            ps.setInt(2, indexInput);
                            ResultSet rs3 = ps.executeQuery();
                            ps.closeOnCompletion();
                            ArrayList<FieldValueLimitation> lims = new ArrayList<>();
                            while(rs3.next()){
                                FieldValueLimitation lim = new FieldValueLimitation();
                                for(CMTField fi: event.getFields()){
                                    if(fi.isIsVar() && fi.getSql_id() == rs3.getInt("field_id")){
                                        lim.setFieldName(fi.getName());
                                        lim.setOperator(rs3.getString("operator"));
                                lim.setValue(rs3.getString("value"));
                                        break;
                                    }
                                }
                                
                                lims.add(lim);
                            }
                            rs3.close();
                            event.setLimitations(lims);
                            for(Binding binding : temp.getOutput().getBindings()){
                                System.out.println("- voor if ");
                                if(binding.getStartBinding() instanceof BindingInputFact && ((BindingInputFact)binding.getStartBinding()).getInputObject() instanceof EventInput){
                                    System.out.println(" -in if binding");
                                    ((EventInput)((BindingInputFact)binding.getStartBinding()).getInputObject()).setLimitations(lims);
                                }else{
                                    if(binding.getStartBinding() instanceof BindingInputField && ((BindingInputField)binding.getStartBinding()).getInputObject() instanceof EventInput){
                                        System.out.println(" -in if binding");
                                        ((EventInput)((BindingInputField)binding.getStartBinding()).getInputObject()).setLimitations(lims);
                                    }
                                }
                            
                            }
                    }
                }
            }
            
            
            
            conn.close();
            return temp;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return null;
   }
   @Override
   public void addRule(Rule rule, Template temp){
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rules (template_id, rule_name, rule_drl) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, temp.getSql_id());
            ps.setString(2, rule.getName());
            ps.setString(3, rule.getDrlRule());
            ps.executeUpdate();
            ResultSet rs= ps.getGeneratedKeys();
            rs.next();
            int ruleId = rs.getInt(1);
            rs.close();
            ps.close();
            // set values inputblocks 
            // 1 function filled in fact 
            // 2 function filled in event
            // 3 ifbl is event
            for(IFBlock ifblock : temp.getIfBlocks()){ // lelijke code herschrijven als er eens tijd is
                if(ifblock.getFunction() != null){
                    // get par binding and add to table
                    for(Binding binding : ifblock.getBindings()){
                        BindingIF startbind = (BindingIF) binding.getStartBinding();
                        ps = conn.prepareStatement("SELECT ifblock_function.idifblock_function, function_parameters.parameter_id FROM ifblock_function "
                                                    + " INNER JOIN function_parameters ON ifblock_function.function_id = function_parameters.function_id "
                                                    + " INNER JOIN parameters ON function_parameters.parameter_id = parameters.idfunction_parameters "
                                                    + " WHERE ifblock_function.template_id =? AND ifblock_function.template_position = ? AND parameters.function_parameter_name = ?");
                        ps.setInt(1, temp.getSql_id());
                        ps.setInt(2, startbind.getIndexObj());
                        ps.setString(3, startbind.getIfParameter());
                        ResultSet rs3 = ps.executeQuery();
                        rs3.next();
                        int ifb = rs3.getInt("idifblock_function");
                        int parId = rs3.getInt("parameter_id");
                        rs3.close();
                        ps.close();
                        if(binding.getEndBinding() instanceof BindingInputFact){
                            BindingInputFact bindFact = (BindingInputFact) binding.getEndBinding(); 
                            if(bindFact.getInputObject() instanceof Fact){
                                // add to table rule fact
                                ps = conn.prepareStatement("INSERT INTO rule_param_filledin_fact (rule_id,ifblock_id,param_id,fact_id,index_input) VALUES(?,?,?,?,?)");
                                ps.setInt(1, ruleId);
                                ps.setInt(2, ifb);
                                ps.setInt(3, parId);
                                ps.setInt(4, ((Fact)bindFact.getInputObject()).getId());
                                ps.setInt(5, bindFact.getIndexObj());
                                ps.executeUpdate();
                                ps.close();
                            }else{
                                if(bindFact.getInputObject() instanceof EventInput){
                                    ps = conn.prepareStatement("INSERT INTO rule_param_filledin_event (rule_id,ifblock_id,param_id,facttype_id,index_input) VALUES(?,?,?,?,?)");
                                    ps.setInt(1, ruleId);
                                    ps.setInt(2, ifb);
                                    ps.setInt(3, parId);
                                    ps.setString(4, ((EventInput)bindFact.getInputObject()).getClassName());
                                    ps.setInt(5, bindFact.getIndexObj());
                                    ps.executeUpdate();
                                    ps.close();
                                }
                            }
                        }else{
                            if(binding.getEndBinding() instanceof BindingInputField){
                                BindingInputField bindField = (BindingInputField) binding.getEndBinding();
                                if(bindField.getInputObject() instanceof Fact){
                                // add to table rule fact
                                ps = conn.prepareStatement("INSERT INTO rule_param_filledin_fact (rule_id,ifblock_id,param_id,fact_id,index_input, field_id) VALUES(?,?,?,?,?, ?)");
                                ps.setInt(1, ruleId);
                                ps.setInt(2, ifb);
                                ps.setInt(3, parId);
                                ps.setInt(4, ((Fact)bindField.getInputObject()).getId());
                                ps.setInt(5, bindField.getIndexObj());
                                ps.setInt(6, bindField.getField().getSql_id());
                                ps.executeUpdate();
                                ps.close();
                            }else{
                                if(bindField.getInputObject() instanceof EventInput){
                                    ps = conn.prepareStatement("INSERT INTO rule_param_filledin_event (rule_id,ifblock_id,param_id,facttype_id,index_input, field_id) VALUES(?,?,?,?,?,?)");
                                    ps.setInt(1, ruleId);
                                    ps.setInt(2, ifb);
                                    ps.setInt(3, parId);
                                    ps.setString(4, ((EventInput)bindField.getInputObject()).getClassName());
                                    ps.setInt(5, bindField.getIndexObj());
                                    ps.setInt(6, bindField.getField().getSql_id());
                                    ps.executeUpdate();
                                    ps.close();
                                }
                            }
                            }
                        }
                    }
                }else{
                    if(ifblock.getEvent() != null){
                        BindingIF startBind = (BindingIF) ifblock.getBindings().get(0).getStartBinding();
                        int indexInput = ((BindingInputFact)ifblock.getBindings().get(0).getEndBinding()).getIndexObj();
                        EventInput event = (EventInput)((BindingInputFact)ifblock.getBindings().get(0).getEndBinding()).getInputObject();
                        // get ifblo event if 
                        ps = conn.prepareStatement("SELECT idifblock_event FROM ifblock_event WHERE template_id = ? AND template_position = ? AND facttype_event = ?");
                        ps.setInt(1, temp.getSql_id());
                        ps.setInt(2, ifblock.getBindings().get(0).getStartBinding().getIndexObj());
                        ps.setString(3, event.getClassName());
                        ResultSet rs2 = ps.executeQuery();
                        rs2.next();
                        int ifblId = rs2.getInt("idifblock_event");
                        rs2.close();
                        ps.close();
                        ps = conn.prepareStatement("INSERT INTO rule_limits_ifblock_event (rule_id, ifblock_event_id, field_id, operator, value, index_input) VALUES(?,?,?,?,?,?)");
                        for(FieldValueLimitation lim : event.getLimitations()){
                            for(CMTField field : event.getFields()){
                                if(field.getName().equals(lim.getFieldName())){
                                    ps.setInt(3, field.getSql_id());
                                    break;
                                }
                            }
                            ps.setInt(1, ruleId);
                            ps.setInt(2,ifblId);
                            ps.setString(4, lim.getOperator() );
                            ps.setString(5, lim.getValue());
                            ps.setInt(6, indexInput);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        ps.closeOnCompletion();
                    }
                
                }
            }

            if(temp instanceof TemplateHA){
                TemplateHA tempSitu = (TemplateHA) temp;
                ps = conn.prepareStatement("INSERT INTO rule_situation VALUES(?,?)");
                ps.setInt(1, ruleId);
                ps.setString(2,tempSitu.getOutput().getName());
                ps.executeUpdate();
                ps.close();
                
            }else{
                // update actions
            }            
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   @Override
   public ArrayList<FactType> getCustomEventsUsedInTemplate(int templateId){

       ArrayList<FactType> result = new ArrayList<>();

        try {

            Connection conn = ds.getConnection();

            // check ifblock for events

            PreparedStatement ps = conn.prepareStatement("SELECT facttype.facttypeName FROM ifblock_event "

                                                        + " INNER JOIN facttype ON ifblock_event.facttype_event = facttype.facttypeName "

                                                        + " WHERE ifblock_event.template_id = ? AND facttype.isCustom = ?");

            ps.setInt(1, templateId);

            ps.setInt(2, 1);

            ps.closeOnCompletion();

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                result.add(getFactTypeWithName(rs.getString("facttypeName")));

                Template temp = getTemplateOfSituation(rs.getString("facttypeName"));

                ArrayList<FactType> types = getCustomEventsUsedInTemplate(temp.getSql_id());

                result.addAll(types);

            }

            rs.close();

           

            // for functions no need -- if the event is used as a parameter then it is also as an ifblock event!

           

            conn.close();

        } catch (SQLException ex) {

            Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);

        }

      

       

       return result;

   }
    
}
