package telran.employees;

import java.util.*;
//So far we do consider optimization
public class CompanyMapsImpl implements Company {
	
	TreeMap<Long,Employee> employees = new TreeMap<>();
	TreeMap<String, List<Employee>> employeesDepartment = new TreeMap<>();
	TreeMap<Float, List<Manager>> factorManagers = new TreeMap<>();

	@Override
	public Iterator<Employee> iterator() {
		return employees.values().iterator();
	}

	@Override
	public void addEmployee(Employee empl) {
		if (employees.putIfAbsent(empl.getId(), empl) == null) {
			employeesDepartment.computeIfAbsent(empl.getDepartment(), emp -> new LinkedList<>()).add(empl);
			if ( empl instanceof Manager ) {
				Manager manager = (Manager)empl;
				factorManagers.computeIfAbsent(manager.factor, emp -> new LinkedList<>()).add(manager);
			}
		} else {
			throw new IllegalStateException();
		}

	}

	@Override
	public Employee getEmployee(long id) {
		return employees.get(id);
	}

	@Override
	public Employee removeEmployee(long id) {
		Employee result = null;
		if ( ( result = employees.remove(id) ) != null ) {
			employeesDepartment.get(result.getDepartment()).remove(result);
			if ( result instanceof Manager ) {
				Manager manager = (Manager)result;
				factorManagers.get(manager.factor).remove(manager);
			}
		} else {
			throw new NoSuchElementException();
		}
		return result;
	}

	@Override
	public int getDepartmentBudget(String department) {
		return employeesDepartment.getOrDefault(department, new LinkedList<>()).stream().mapToInt(Employee::computeSalary).sum();
	}

	@Override
	public String[] getDepartments() {
		return employeesDepartment.keySet().stream().toArray(String[]::new);
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		return factorManagers.lastEntry().getValue().stream().toArray(Manager[]::new);
	}

}
