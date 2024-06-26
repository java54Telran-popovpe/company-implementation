package telran.employees;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
//So far we do consider optimization
public class CompanyMapsImpl implements Company {
	
	TreeMap<Long,Employee> employees = new TreeMap<>();
	TreeMap<String, List<Employee>> employeesDepartment = new TreeMap<>();
	TreeMap<Float, List<Manager>> factorManagers = new TreeMap<>();

	@Override
	public Iterator<Employee> iterator() {
		return new Iterator<Employee>() {
			Iterator<Employee> iterator = employees.values().iterator();
			Employee prev = null;
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Employee next() {
				prev = iterator.next();
				return prev;
			}
			@Override
			public void remove() {
				iterator.remove();
				updateAfterEmployeeDeletion(prev);
			}
			
		};
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
		Employee result = employees.remove(id);
		if ( result == null ) {
			throw new NoSuchElementException();
		}
		updateAfterEmployeeDeletion(result);
		return result;
	}

	private void updateAfterEmployeeDeletion(Employee employee) {
		updateMapList(employeesDepartment, employee, Employee::getDepartment);
		if ( employee instanceof Manager ) {
			Manager manager = (Manager)employee;
			updateMapList(factorManagers, manager, el -> el.factor );
		}
	}
	
	private <T, U> void updateMapList ( Map<T,List<U>> map, U elementToDelete,Function<U,T> mapFunc ) {
		T key = mapFunc.apply(elementToDelete);
		map.computeIfPresent(key, (k, l) -> {
			l.remove(elementToDelete);
			return l.isEmpty() ? null : l;
		});
	}

	@Override
	public int getDepartmentBudget(String department) {
		return employeesDepartment.getOrDefault(department, new LinkedList<>()).stream().mapToInt(Employee::computeSalary).sum();
	}

	@Override
	public String[] getDepartments() {
		return employeesDepartment.keySet().toArray(String[]::new);
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		return factorManagers.lastEntry().getValue().toArray(Manager[]::new);
	}

}
