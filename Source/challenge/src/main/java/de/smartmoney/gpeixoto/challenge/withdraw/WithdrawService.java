package de.smartmoney.gpeixoto.challenge.withdraw;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import de.smartmoney.gpeixoto.challenge.shared.BusinessException;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;

@Service
public class WithdrawService {

	private final WithdrawRepository repository;
	private final UserRepository userRepository;
	
	public WithdrawService(UserRepository userRepository, WithdrawRepository repository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	public Optional<Withdraw> findById(Long id) {
		return repository.findById(id);
	}

	public Long countLast24Hours(User user) {
		Instant after = Instant.now().minus(24, ChronoUnit.HOURS);
		return repository.countByUserAndCreatedDateAfter(user, after);
	}

	@Transactional
	public Withdraw save(Withdraw withdraw) {

		Optional<User> optional = userRepository.findByEmail(withdraw.getUser().getEmail());
		
		if(optional.isEmpty()) {
			throw new BusinessException("user", "User not found");
		}
		
		User user = optional.get();
		
		withdraw.setUser(user);

		repository.lockByUser(user.getId());

		Long count = repository.countByUser(user);

		if (count == 0 && withdraw.getValue().compareTo(new BigDecimal("50.00")) > 0) {
			throw new BusinessException("value", "Your first withdraw is limited to $50.00");
		}

		if (countLast24Hours(user) >= 5) {
			throw new BusinessException("value", "You have reached the daily withdrawals limit");
		}

		Integer percentage;

		if (withdraw.getValue().compareTo(new BigDecimal("100.99")) <= 0) {
			percentage = 3;
		} else if (withdraw.getValue().compareTo(new BigDecimal("250.99")) <= 0) {
			percentage = 2;
		} else {
			percentage = 1;
		}

		withdraw.setFee(withdraw.getValue().multiply(new BigDecimal(percentage)).divide(new BigDecimal(100)));

		return repository.save(withdraw);
	}
}